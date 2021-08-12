package com.icent.isaver.critical.component;

import com.icent.isaver.core.component.MqttCore;
import com.icent.isaver.core.dao.postgresql.NotificationDao;
import com.icent.isaver.critical.service.EventSvc;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * The type MQTT socket svc.
 */
@Slf4j
@Component
public class MqttComp extends MqttCore {

    @Autowired
    private EventSvc eventSvc;

    @Autowired
    private NotificationDao notificationDao;

    @PostConstruct
    public void init() {
        super.addTopic(new String[]{"addEvent","signal/req/+","led/req/+"});
        super.connect();
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        String msg = new String(mqttMessage.getPayload(), "UTF-8");

        String[] topicArr = topic.split("/");

        if(topicArr.length>0){
            String actionType = null;
            String siteId = null;
            if(topicArr.length>1){ actionType = topicArr[1]; }
            if(topicArr.length>2){ siteId = topicArr[2]; }

            switch (topicArr[0]){
                case "addEvent" :
                    log.info("[MqttComp][{}] input : {}", topic, msg);
                    eventSvc.addEvent(msg);
                    break;
                case "signal" :
                    if(actionType!=null && actionType.equals("req") && siteId!=null){
                        try{
                            JSONObject jsonParam = (JSONObject) new JSONParser().parse(msg);
                            Map notiParam = new HashMap();
                            notiParam.put("areaId", siteId);
                            jsonParam.put("remainCnt",notificationDao.findRemainCnt(notiParam));
                            publish("signal/res/"+siteId,jsonParam.toJSONString(),0);
                        }catch(Exception e){
                            log.error("[MqttComp][{}] Json Parse Error : {}",topic,e.getMessage());
                        }
                    }
                    break;
                case "led" :
                    if(actionType!=null && actionType.equals("req") && siteId!=null){
                        try{
                            JSONObject jsonParam = new JSONObject();
                            Map led1Param = new HashMap();
                            led1Param.put("areaId", siteId);
                            led1Param.put("fenceIds", "{06af484f-4f37-f5da-94b2-2920f1ad9575},{4a5185c1-934d-7211-0353-fa6b3d8da631}".split(","));
                            jsonParam.put("led1",notificationDao.findRemainCntCustom(led1Param));

                            Map led2Param = new HashMap();
                            led2Param.put("areaId", siteId);
                            led2Param.put("fenceIds", "7dfc208f-1e09-c594-bde0-4aa2401ddad5,{7f890d45-c13d-0561-d990-7c8e06bc87ee},62b52a9d-c519-d60f-d9a8-9108e9a944a3".split(","));
                            jsonParam.put("led2",notificationDao.findRemainCntCustom(led2Param));

                            Map led3Param = new HashMap();
                            led3Param.put("areaId", siteId);
                            led3Param.put("fenceIds", "{e322fe6f-ae2a-e873-1a1f-fe3d3c9cc391},{7f890d45-c13d-0561-d990-7c8e06bc87ee}".split(","));
                            jsonParam.put("led3",notificationDao.findRemainCntCustom(led3Param));
                            publish("led/res/"+siteId,jsonParam.toJSONString(),0);
                        }catch(Exception e){
                            log.error("[MqttComp][{}] Json Parse Error : {}",topic,e.getMessage());
                        }
                    }
                    break;
            }
        }
    }
}
