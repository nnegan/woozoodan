package com.wzd.common.advice;


import com.wzd.common.model.CommonDataModel;
import com.wzd.common.model.CommonResponseModel;
import com.wzd.common.model.PagenatedListModel;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 응답 포메팃
 */
@RestControllerAdvice
@RestController
@ResponseBody
@Api(description = "응답 포메팃")
public class CommonRestResponseAdvice
        implements ResponseBodyAdvice<Object>, ErrorController, InitializingBean {

    public static final String ERROR_PATH					= "/error";

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                      ServerHttpRequest request, ServerHttpResponse response) {
        CommonResponseModel rv;

        if (body instanceof CommonResponseModel) {
            rv	= (CommonResponseModel)body;
        } else if (body instanceof PagenatedListModel) {
            rv	= (PagenatedListModel<?>)body;
        } else {
            rv	= new CommonResponseModel<Object>();
            if (body instanceof CommonDataModel) {
                rv.setData(((CommonDataModel<?>)body).getData());
            } else {
                rv.setData(body);
            }
        }

        return rv;
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }
}

