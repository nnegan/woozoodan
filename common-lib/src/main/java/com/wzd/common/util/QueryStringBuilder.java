package com.wzd.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.UriComponentsBuilder;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * 쿼리 문자열 생성기 클래스
 */
@Slf4j
public class QueryStringBuilder {

    protected Map<String, Object> params = new TreeMap<String, Object>();

    /**
     * 쿼리 문자열에 퍼블릭 게터를 매개변수로 추가한다.
     *
     * @param o 쿼리 매개변수로 사용할 객체
     * @return 메서드 체이닝을 위해 QueryStringBuilder를 반환
     */
    public QueryStringBuilder add(Object o) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(o.getClass(), Object.class);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            if (propertyDescriptors != null) {
                for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    String name = propertyDescriptor.getName();
                    Method method = propertyDescriptor.getReadMethod();
                    if (method != null) {
                        try {
                            Object value = method.invoke(o);
                            if (value != null) {
                                add(name, value);

                            }
                        } catch (IllegalAccessException x) {
                            throw new IllegalArgumentException(x);
                        } catch (InvocationTargetException x) {
                            throw new IllegalArgumentException(x);
                        }
                    } else {
                        log.debug(" ######### method is null ######### ");
                    }
                }
            } else {
                log.debug(" ######### propertyDescriptors is null ######### ");
            }

            return this;
        } catch (IntrospectionException x) {
            throw new IllegalArgumentException(x);
        }
    }

    /**
     * 주어진 빈에 정의된 메서드의 실행 결괏값을 기존 URL에 쿼리 문자열 형태로 덧붙인다.
     *
     * @param o 객체
     * @param uri URI
     * @param path 경로
     * @return 메서드 체이닝을 위해 UriComponentsBuilder를 반환
     */
    public UriComponentsBuilder create(Object o, String uri, String path) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri).path(path);
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(o.getClass(), Object.class);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            if (propertyDescriptors != null) {
                for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    String name = propertyDescriptor.getName();
                    Method method = propertyDescriptor.getReadMethod();
                    if (method != null) {
                        try {
                            Object value = method.invoke(o);
                            if (value != null) {
                                builder.queryParam(name, value);
                            }
                        } catch (IllegalAccessException x) {
                            throw new IllegalArgumentException(x);
                        } catch (InvocationTargetException x) {
                            throw new IllegalArgumentException(x);
                        }
                    } else {
                        log.debug(" ######### method is null ######### ");
                    }
                }
            } else {
                log.debug(" ######### propertyDescriptors is null ######### ");
            }

            return builder;
        } catch (IntrospectionException x) {
            throw new IllegalArgumentException(x);
        }
    }

    /**
     * 쿼리 문자열에 매개변수를 추가한다.
     *
     * @param name 매개변수명
     * @param value 매개변숫값
     * @return 메서드 체이닝을 위해 QueryStringBuilder를 반환
     */
    public QueryStringBuilder add(String name, Object value) {
        params.put(name, value);
        return this;
    }

    /**
     * 매개변수 맵을 반환한다.
     *
     * @return 매개변수 맵
     */
    public Map<String, Object> getParams() {
        return params;
    }

    protected void append(StringBuilder sb, String key, Object value) {
        try {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(key).append("=").append(URLEncoder.encode(value.toString(), "UTF-8"));
        } catch (UnsupportedEncodingException x) {
            throw new IllegalStateException(x); // this should never happen
        }
    }

    /**
     * HTTP GET 요청 시 사용할 쿼리 문자열을 반환한다.
     *
     * @return 쿼리 문자열
     */
    public String toQueryString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            String key = entry.getKey();
            if (value instanceof Object[]) {
                for (Object ovalue : ((Object[]) value)) {
                    append(sb, key, ovalue);
                }
            } else if (value instanceof Collection) {
                for (Object ovalue : ((Collection<?>) value)) {
                    append(sb, key, ovalue);
                }
            } else {
                append(sb, key, value);
            }
        }
        return sb.toString();
    }

}