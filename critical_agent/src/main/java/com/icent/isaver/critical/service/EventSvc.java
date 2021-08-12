package com.icent.isaver.critical.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.icent.isaver.core.bean.*;
import com.icent.isaver.core.dao.mongo.EventLogMapper;
import com.icent.isaver.core.dao.postgresql.*;
import com.icent.isaver.core.resource.AgentResource;
import com.icent.isaver.core.util.AgentHelper;
import com.icent.isaver.core.util.StringUtils;
import com.icent.isaver.core.util.TransactionUtil;
import com.icent.isaver.critical.component.EventCancelComp;
import com.icent.isaver.critical.component.MqttComp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Event Service implements
 *
 * @author : psb
 * @version : 1.0
 * @since : 2019. 10. 29.
 * <pre>
 *
 * == 개정이력(Modification Information) ====================
 *
 *  수정일            수정자         수정내용
 * -------------- ------------- ---------------------------
 *  2019. 10. 29.     psb           최초 생성
 * </pre>
 */
@Slf4j
@Service
public class EventSvc {
    @Value("${file.server.address}")
    private String fileAddress = null;

    @Value("${file.server.fileAttachedUploadPath}")
    private String fileAttachedUploadPath = null;

    @Autowired
    private EventValidateDao eventValidateDao;

    @Autowired
    private CriticalDao criticalDao;

    @Autowired
    private CriticalBlockDao criticalBlockDao;

    @Autowired
    private FenceDao fenceDao;

    @Autowired
    private NotificationDao notificationDao;

    @Autowired
    private EventLogMapper eventLogMapper;

    @Autowired
    private EventCancelComp eventCancelComp;

    @Autowired
    private MqttComp mqttComp;

    @Resource(name = "txManager")
    private DataSourceTransactionManager transactionManager;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public void addEvent(String msg) {
        try{
            JsonParser parser = new JsonParser();
            JsonElement elem = parser.parse(msg);

            if(elem.isJsonArray()){
                JsonArray jsonArr = elem.getAsJsonArray();
                for (JsonElement eventObj : jsonArr) {
                    Map result = validate(eventObj);
                    if(result!=null){
                        addMongoDB(result);
                        sendEvent(result);
                        criticalCheck(result);
                    }
                }
            }else{
                Map result = validate(elem);
                if(result!=null){
                    addMongoDB(result);
                    sendEvent(result);
                    criticalCheck(result);
                }
            }
        }catch(Exception e){
            log.error("[addEvent] error - {}",e.getMessage());
        }
    }

    // 필수값 체크
    private Map validate(JsonElement jsonObj){
        Map event = new Gson().fromJson(jsonObj, Map.class);
        try{
            if(event==null
                    || !event.containsKey("deviceId")
                    || !event.containsKey("eventId")
                    || !event.containsKey("eventDatetime")){
                return null;
            }
            event.put("eventDatetime",dateFormat.parse(event.get("eventDatetime").toString()));
        }catch(Exception e){
            log.error("[validate] parameter check error - {}",e.getMessage());
        }

        EventValidateBean eventValidateBean = eventValidateDao.find(event);

        if(eventValidateBean==null){
            log.warn("[validate] The device information of the condition does not exist.");
            return null;
        }else{
            event.put("eventLogId", StringUtils.getGUID32());
            event.put("areaId",eventValidateBean.getAreaId());
            event.put("areaName",eventValidateBean.getAreaName());
            event.put("deviceId",eventValidateBean.getDeviceId());
            event.put("deviceName",eventValidateBean.getDeviceName());
            event.put("eventName",eventValidateBean.getEventName());
            event.put("fenceId",eventValidateBean.getFenceId());
            event.put("fenceName",eventValidateBean.getFenceName());
        }
        return event;
    }

    // 일반 이벤트 MongoDB 저장
    private void addMongoDB(Map event){
        try{
            eventLogMapper.insertOne(event);
        }catch (Exception e){
            log.error("[addMongoDB] Insert Error - {}",e.getMessage());
        }
    }

    // 일반 이벤트 전송
    private void sendEvent(Map event){
        Map eventWsMap = new HashMap();
        eventWsMap.put("messageType", "addEvent");
        eventWsMap.put("eventLog", event);
        try {
            ObjectMapper mapper = new ObjectMapper();
            mqttComp.publish("eventLog",mapper.writeValueAsString(eventWsMap),0);
        } catch (Exception e) {
            log.error("[sendEvent] send Event Error - {}",e.getMessage());
        }
    }

    // 임계치 체크
    private void criticalCheck(Map event){
        SimpleDateFormat hmsFormat = new SimpleDateFormat("HH:mm:ss");
        double value = 0;
        try{
            if(event.get("value")!=null){
                value = Double.parseDouble(event.get("value").toString());
            }
        }catch(Exception e){
            log.warn(e.getMessage());
        }
        event.put("value",value);
        event.put("eventDatetimeHMS",hmsFormat.format(event.get("eventDatetime")));

        /**
         * 임계치 차단 체크
         * @author psb
         */
        CriticalBlockBean criticalBlock = criticalBlockDao.find(event);
        if(criticalBlock==null){
            /**
             * 임계치 체크
             * @author psb
             */
            CriticalBean critical = criticalDao.find(event);
            if(critical!=null) {
                Map criticalWsMap = new HashMap();
                NotificationBean notificationParam = AgentHelper.convertMapToBean(event, NotificationBean.class, "yyyy-MM-dd HH:mm:ss.SSS");
                notificationParam.setCriticalLevel(critical.getCriticalLevel());
                notificationParam.setCriticalTargetList(critical.getCriticalTargetList());

                if (StringUtils.notNullCheck(critical.getPhysicalFileName())) {
                    try{
                        InetAddress address = InetAddress.getByName(fileAddress);
                        criticalWsMap.put("dashboardAlarmFileUrl", "http://" + address.getHostAddress() + fileAttachedUploadPath + critical.getPhysicalFileName());
                    }catch(UnknownHostException e){
                        log.error("[criticalCheck] UnknownHostException - {}", e.getMessage());
                    }
                }

                // 해지이벤트
                if (critical.getCriticalLevel().equals(AgentResource.CRITICAL_CANCEL_CODE)) {
                    notificationParam.setStatus("C");
                    criticalWsMap.put("messageType", "cancelDetection");

                    notificationParam.setLastValue(notificationParam.getValue());
                    if(StringUtils.notNullCheck(notificationParam.getObjectId())){
                        try {
                            NotificationBean notificationBean = notificationDao.findDetectNotification(notificationParam);
                            if(notificationBean!=null){
                                notificationParam.setValue(notificationBean.getValue());
                                notificationParam.setCancelCriticalLevel(notificationBean.getCriticalLevel());
                            }
                            eventCancelComp.addCancelModel(notificationParam);
                        } catch (Exception e) {
                            log.error("[criticalCheck] Mqtt Publish error - {}", e.getMessage());
                        }
                    }
                } else { // 임계치 이벤트
                    notificationParam.setStatus("D");
                    criticalWsMap.put("messageType", "addNotification");
                    notificationParam.setNotificationId(StringUtils.getGUID32());

                    if(StringUtils.notNullCheck(notificationParam.getFenceId())){
                        FenceBean fenceParam = new FenceBean();
                        fenceParam.setDeviceId(notificationParam.getDeviceId());
                        fenceParam.setFenceId(notificationParam.getFenceId());
                        List<FenceBean> fenceList = fenceDao.findListCamera(fenceParam);
                        if(fenceList!=null){
                            notificationParam.setFenceDeviceList(fenceList);
                        }
                        FenceBean fence = fenceDao.findByFence(fenceParam);
                        if(fence!=null){
                            notificationParam.setFenceType(fence.getFenceType());
                        }
                    }
                    addNotification(notificationParam);
                }

                /**
                 * 알림 전송
                 * @author psb
                 */
                criticalWsMap.put("notification",notificationParam);
                criticalWsMap.put("location",event.get("location"));
                try {
                    mqttComp.publish("eventAlarm",new ObjectMapper().writeValueAsString(criticalWsMap),0);
                } catch (Exception e) {
                    log.error("[criticalCheck] Mqtt Publish error - {}", e.getMessage());
                }
            }
        }
    }

    // 알림센터 Postgresql 저장
    private void addNotification(NotificationBean notificationParam){
        TransactionStatus transactionStatus = TransactionUtil.getMybatisTransactionStatus(transactionManager);
        try{
            notificationDao.addNotification(notificationParam);
            transactionManager.commit(transactionStatus);
            log.info("[addNotification] Insert Success - notificationId : {}", notificationParam.getNotificationId());
        }catch(DataAccessException e){
            transactionManager.rollback(transactionStatus);
            log.error("[addNotification] Insert Error - notificationId : {}, detail : {}", notificationParam.getNotificationId(), e.getMessage());
        }
    }
}
