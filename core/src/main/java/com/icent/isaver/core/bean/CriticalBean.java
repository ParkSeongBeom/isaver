package com.icent.isaver.core.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Critical Bean
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
public class CriticalBean {
    private String criticalId;
    private String eventId;
    private String criticalLevel;
    private Double criticalValue;
    private String dashboardFileId;

    /**
     * Etc
     */
    private List<CriticalTargetBean> criticalTargetList;
    private String physicalFileName;
}
