package com.icent.isaver.core.dao.postgresql;

import com.icent.isaver.core.bean.CriticalBlockBean;

import java.util.Map;

/**
 * Critical Dao Interface
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
public interface CriticalBlockDao {
    CriticalBlockBean find(Map<String, String> parameters);
}
