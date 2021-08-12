package com.icent.isaver.core.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Fence Bean
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
public class FenceBean {

    /* UUID*/
    private String uuid;
    /* 펜스 ID*/
    private String fenceId;
    /* 펜스타입*/
    private String fenceType;
    /* 펜스 상세 타입*/
    private String fenceSubType;
    /* 장치 ID*/
    private String deviceId;
    /* 설정정보 */
    private String config;
    /* 설정정보 */
    private String customJson;

    /* 펜스명*/
    private String fenceName;

    /**
     * Etc
     */
    /* IP */
    private String ipAddress;

    private List<FenceLocationBean> location;
}
