package com.wzd.gateway.filter;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.zuul.context.RequestContext;

@RestController
public class ErrorHandlerController implements ErrorController {

    private static final String ERROR_MAPPING = "/error";

    @RequestMapping(value = ERROR_MAPPING)
    public ResponseEntity<String> error() {

        RequestContext ctx = RequestContext.getCurrentContext();
        Object error = ExceptionUtils.getRootCause((Exception) ctx.get("throwable"));

        // zuul.routes.{proxy}.path 에 정의되지 않은 요청일 경우 응답 처리
        if (error == null) {

            return new ResponseEntity<String>("NOT_FOUND", HttpStatus.NOT_FOUND);
        }

        if (error instanceof Exception) {

            return new ResponseEntity<String>("SERVICE_UNAVAILABLE", HttpStatus.SERVICE_UNAVAILABLE);
        }

        // 예상되지 않은 오류일 경우 응답 처리
        return new ResponseEntity<String>("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public String getErrorPath() {

        return ERROR_MAPPING;
    }
}
