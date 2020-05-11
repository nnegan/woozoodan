package com.wzd.common.util;

import org.slf4j.MDC;
import org.slf4j.spi.MDCAdapter;

import java.util.Map;

/**
 * MDC(Mapped Diagnostic Context) 유틸리티 클래스
 */
public class MDCUtils {

    private static MDCAdapter mdc = MDC.getMDCAdapter();

    /**
     * HEADER_MAP
     */
    public static final String HEADER_MAP = "HEADER_MAP";

    /**
     * PARAMETER_MAP
     */
    public static final String PARAMETER_MAP = "PARAMETER_MAP";

    /**
     * TX_ID
     */
//    public static final String TX_ID = "TX_ID";
    
    /**
     * TRACE_ID
     */
    public static final String TRACE_ID = "TRACE_ID";
    
    /**
     * SERVICE_NAME
     */
    public static final String SERVICE_NAME = "SERVICE_NAME";
    
    /**
     * TAG
     */
    public static final String TAG = "TAG";
    
    /**
     * ACTION
     */
    public static final String ACTION = "ACTION";

    /**
     * LANG
     */
    public static final String LANG = "LANG";

    /**
     * USER_INFO
     */
    public static final String USER_INFO = "USER_INFO";

    /**
     * REQUEST_URI
     */
    public static final String REQUEST_URI = "REQUEST_URI";

    /**
     * AGENT_DETAIL
     */
    public static final String AGENT_DETAIL = "AGENT_DETAIL";

    /**
     * 키-값을 세팅한다.
     *
     * @param key 키
     * @param value 값
     */
    public static void set(String key, String value) {
        mdc.put(key, value);
    }

    /**
     * 주어진 키 값을 JSON 형식으로 바꾸어 세팅한다.
     *
     * @param key 키
     * @param value 값
     */
    public static void setJsonValue(String key, Object value) {
        if (value != null) {
            String json = JsonUtils.toJson(value);
            mdc.put(key, json);
        }
    }

    /**
     * 주어진 키에 해당하는 값을 얻는다.
     *
     * @param key 키
     * @return 키에 해당하는 값
     */
    public static String get(String key) {
        return mdc.get(key);
    }

    /**
     * MDC를 초기화한다.
     *
     */
    public static void clear() {
        MDC.clear();
    }

    /**
     * 에러 속성값을 세팅한다.
     *
     * @param errorAttribute 에러 속성 맵
     */
    public static void setErrorAttribute(Map<String, Object> errorAttribute) {
        if (errorAttribute.containsKey("path")) {
            set(REQUEST_URI, (String) errorAttribute.get("path"));
        }
    }
}