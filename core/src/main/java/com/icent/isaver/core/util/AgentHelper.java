package com.icent.isaver.core.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Helper Utils
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
public class AgentHelper {
    /**
     * Map을 해당 Class의 instance를 생성하여 convert한다.
     *
     * @author psb
     * @param parameters
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T convertMapToBean(Map<String, String> parameters, Class<T> clazz){
        return convertMapToBean(parameters, clazz, false, "yyyy-MM-dd");
    }

    /**
     * Map을 해당 Class의 instance를 생성하여 convert한다.
     *
     * @author psb
     * @param parameters
     * @param clazz
     * @param datePattern
     * @param <T>
     * @return
     */
    public static <T> T convertMapToBean(Map<String, String> parameters, Class<T> clazz, String datePattern){
        return convertMapToBean(parameters, clazz, false, datePattern);
    }

    /**
     * Map을 해당 Class의 instance를 생성하여 convert한다.
     *
     * @author psb
     * @param parameters
     * @param clazz
     * @param emptyNullFlag
     * @param <T>
     * @return
     */
    public static <T> T convertMapToBean(Map<String, String> parameters, Class<T> clazz, boolean emptyNullFlag, String datePattern){
        if(emptyNullFlag){
            List<String> removeKeys = new ArrayList<>();
            for(String key : parameters.keySet()){
                if(StringUtils.nullCheck(parameters.get(key))){
                    removeKeys.add(key);
                }
            }
            for(String key : removeKeys){
                parameters.remove(key);
            }
        }

        T bean = null;
        try {
            DateConverter dateConverter = new DateConverter();
            dateConverter.setPattern(datePattern);
            ConvertUtils.register(dateConverter, Date.class);

            bean = BeanUtils.convertMapToBean(parameters, clazz);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            log.error(e.getMessage());
        }
        return bean;
    }
}
