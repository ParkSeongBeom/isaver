package com.icent.isaver.core.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 공통 Bean
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
public class CommonBean {

    /* */
    private String insertUserId;
    /* */
    private String insertUserName;
    /* */
    private Date insertDatetime;
    /* */
    private String updateUserId;
    /* */
    private Date  updateDatetime;

    private String updateUserName;
}
