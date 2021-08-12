package com.icent.isaver.core.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * Notification Bean
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
@Getter
@Setter
public class NotificationBean extends CommonBean {

    /* 알림 ID */
    private String notificationId;
    /* 이벤트 로그 ID */
    private String eventLogId;
    /* Object ID*/
    private String objectId;
    /* Fence ID*/
    private String fenceId;
    /* 상태 */
    private String status;
    /* 구역 ID*/
    private String areaId;
    /* 장치 ID */
    private String deviceId;
    /* 이벤트 ID*/
    private String eventId;
    /* 이벤트 발생 일시 */
    private Date eventDatetime;
    /* 확인 ID */
    private String confirmUserId;
    /* 확인 일시 */
    private Date confirmDatetime;
    /* 해제 ID */
    private String cancelUserId;
    /* 해제 일시 */
    private Date cancelDatetime;
    /* 해제 내용 */
    private String cancelDesc;
    /* 임계치레벨 */
    private String criticalLevel;

    /* ETC */
    /* 구역명 */
    private String fenceType;
    /* 구역명 */
    private String areaName;
    /* 펜스명 */
    private String fenceName;
    /* 장치명 */
    private String deviceName;
    /* 이벤트명 */
    private String eventName;
    /* 확인자 */
    private String confirmUserName;
    /* 해제자 */
    private String cancelUserName;
    /* 갯수 */
    private Integer notiCnt;
    /* value */
    private Double value;
    /* 마지막 value */
    private Double lastValue;
    /* 해제임계치레벨 */
    private String cancelCriticalLevel;
    /* 펜스별 장치 리스트 */
    private List<FenceBean> fenceDeviceList;
    /* 카메라 장치ID 리스트 */
    private List<CriticalTargetBean> criticalTargetList;
}
