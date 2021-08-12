package com.icent.isaver.critical.component;

import com.google.common.collect.Lists;
import com.icent.isaver.core.bean.NotificationBean;
import com.icent.isaver.core.dao.postgresql.NotificationDao;
import com.icent.isaver.core.util.TransactionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
@Component
public class EventCancelComp {

    @Autowired
    private NotificationDao notificationDao;

    private static List<NotificationBean> CANCEL_MODEL = Lists.newArrayList();

    @Resource(name = "txManager")
    private DataSourceTransactionManager transactionManager;

    public void addCancelModel(NotificationBean notificationParam) {
        CANCEL_MODEL.add(notificationParam);
    }

    private void saveNotification(List<NotificationBean> notificationList){
        TransactionStatus transactionStatus = TransactionUtil.getMybatisTransactionStatus(transactionManager);
        try{
            notificationDao.saveNotification(notificationList);
            transactionManager.commit(transactionStatus);
            log.info("[saveNotification] Update Success");
        }catch(DataAccessException e){
            transactionManager.rollback(transactionStatus);
            log.error("[saveNotification] Update Error - detail : {}", e.getMessage());
        }
    }

    @Scheduled(fixedRate = 1000)
    public void cancelEventSchedule() {
        try{
            if(CANCEL_MODEL.size()>0){
                List<NotificationBean> notificationList = new LinkedList<>();
                for(Iterator<NotificationBean> iter = CANCEL_MODEL.iterator(); iter.hasNext();) {
                    NotificationBean notiBean = iter.next();
                    notificationList.add(notiBean);
                    iter.remove();
                }
                saveNotification(notificationList);
            }
        }catch(Exception e){
            log.error("[cancelEventSchedule] error - {}", e.getMessage());
        }
    }
}
