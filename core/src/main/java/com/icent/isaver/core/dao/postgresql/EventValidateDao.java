package com.icent.isaver.core.dao.postgresql;

import com.icent.isaver.core.bean.EventValidateBean;

import java.util.Map;

/**
 * Event Vaildate Dao Interface
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
public interface EventValidateDao {

    EventValidateBean find(Map<String, String> parameters);
}
