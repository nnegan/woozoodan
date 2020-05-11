package com.wzd.common.advice;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.wzd.common.exception.CommonMessageException;
import com.wzd.common.model.CommonDataModel;
import com.wzd.common.model.CommonResponseModel;
import com.wzd.common.model.PagenatedListModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.util.NestedServletException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 응답 포메팃
 */
@Slf4j
@RestControllerAdvice
@RestController
@ResponseBody
@Api(description = "응답 포메팃")
public class CommonRestResponseAdvice
        implements ResponseBodyAdvice<Object>, ErrorController, InitializingBean {

    public static final String DEFAULT_SUCCES_RETURN_CODE	= "SUCCESS";
    public static final String ERROR_PATH					= "/error";

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ServletContext svCtx;

    @Autowired
    private ObjectMapper jsonMapper;

    private String			errorPath;

    private String[]		rawResponsePaths;

    @Override
    public void afterPropertiesSet() throws Exception {
        List<String> rawResponsePathList;
        String			contextPath;

        rawResponsePathList	= new ArrayList<>();
        contextPath			= this.svCtx.getContextPath();
        if ("/".equals(contextPath)) {
            contextPath	= "";
        }
        this.errorPath	= contextPath + ERROR_PATH;
        this.rawResponsePaths	= rawResponsePathList.toArray(new String[rawResponsePathList.size()]);
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
        CommonResponseModel	rv;
        /*HttpStatus          httpStatus;
        String				returnCode;
        String				resMsg;
        String[]			resSubMsgs;
        String				uri;
        Integer				bctxDataCount;*/

        if (MediaType.TEXT_HTML.equals(selectedContentType) || MediaType.TEXT_PLAIN.equals(selectedContentType)) {
            return body;
        }

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public CommonResponseModel<List<ObjectError>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){

        log.debug("invalid input data", e);

        return this.handleValidateException(e.getBindingResult());
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public CommonResponseModel<List<ObjectError>> handleBindException(BindException e){

        log.debug("invalid input data", e);

        return this.handleValidateException(e.getBindingResult());
    }

    private CommonResponseModel<List<ObjectError>> handleValidateException(BindingResult br){
        CommonResponseModel<List<ObjectError>>	rv;
        List<ObjectError>						ivrList;
        List<String>							ivrMessages;
        Locale userLocale;

        userLocale	= LocaleContextHolder.getLocale();

        ivrList		= br.getAllErrors();
        ivrMessages	= new ArrayList<>();
        ivrList.forEach(ivr -> {
            ivrMessages.add(ivr.getDefaultMessage());
        });

        rv	= new CommonResponseModel<>();
        rv.setReturnCode(CommonMessageException.INVALID_INPUT_RETURN_CODE);
        rv.setMessage(this.messageSource.getMessage("server.global.validation-fail", null, userLocale));
        rv.setData(ivrList);
        rv.setDataCount(ivrList.size());
        rv.setSubMessages(ivrMessages.toArray(new String[ivrMessages.size()]));

        return rv;
    }

    @ExceptionHandler(CommonMessageException.class)
    public CommonResponseModel<Object> handleMessageException(CommonMessageException cme, HttpServletResponse res){
        CommonResponseModel<Object>	rv;
        Locale						userLocale;

        if (cme.getHttpStatusCode() >= 500) {
            log.error("bo message exception thrown", cme);
        } else {
            log.info("bo message exception thrown to return code {}", cme.getReturnCode());
        }

        userLocale	= LocaleContextHolder.getLocale();
        rv	= new CommonResponseModel<>();
        rv.setReturnCode(cme.getReturnCode());
        rv.setMessage(this.messageSource.getMessage(cme.getMessage(), cme.getMessageArgs(), userLocale));
        rv.setData(cme.getData());
        res.setStatus(cme.getHttpStatusCode());


        return rv;
    }


    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponseModel<Object> handleUncachedException(Throwable t){
        CommonResponseModel<Object>	rv;
        Locale						userLocale;

        userLocale	= LocaleContextHolder.getLocale();
        rv	= new CommonResponseModel<>();
        rv.setReturnCode(CommonMessageException.DEFAULT_RETURN_CODE);
        rv.setMessage(this.messageSource.getMessage("server.global.process-fail", null, userLocale));


        return rv;
    }

    @RequestMapping(path = ERROR_PATH, produces = {"application/*"})
    @ApiOperation(value="에러 응답 처리", notes="에러 응답 처리를 한다.")
    public CommonResponseModel<Object> renderErrorObj(HttpServletRequest req, HttpServletResponse res)
            throws Throwable {
        CommonResponseModel<Object>	rv;
        Throwable 					thrown;
        Integer						errStatus;
        Locale						userLocale;

        thrown		= (Throwable)req.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        if (thrown != null) {
            if (thrown instanceof NestedServletException) {
                thrown	= ((NestedServletException)thrown).getCause();
            }
            if (thrown instanceof CommonMessageException) {
                return this.handleMessageException((CommonMessageException)thrown, res);
            } else {
                return this.handleUncachedException(thrown);
            }
        }
        userLocale		= LocaleContextHolder.getLocale();
        rv				= new CommonResponseModel<>();
        errStatus		= (Integer)req.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (errStatus == null) {
            rv.setReturnCode(CommonMessageException.DEFAULT_RETURN_CODE);
        } else {
            rv.setReturnCode(String.valueOf(errStatus));
            rv.setMessage(this.messageSource.getMessage("server.global.raw-message", new Object[] {HttpStatus.valueOf(errStatus).toString()}, userLocale));
        }
        return rv;
    }

    @RequestMapping(path = ERROR_PATH, produces = {"*/*"})
    @ApiOperation(value="에러 응답 처리", notes="에러 응답 처리를 한다.")
    public String renderError(HttpServletRequest req, HttpServletResponse res)
            throws Throwable {
        return this.jsonMapper.writeValueAsString(this.renderErrorObj(req, res));
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }
}

