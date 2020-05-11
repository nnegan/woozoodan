package com.wzd.common.exception;

/**
 * 이 익셉션을 발생 시키면 클라이언트로 국제화 메세지 내용을 포함하여 응답합니다.
 * @author pat
 *
 */
@SuppressWarnings("serial")
public class CommonMessageException
	extends RuntimeException {

	public static final int		DEFAULT_HTTP_STATUS_CODE		= 500;
	public static final String	DEFAULT_RETURN_CODE				= "SERVER_ERROR";

	public static final int 	INVALID_INPUT_HTTP_STATUS_CODE	= 422;
	public static final String 	INVALID_INPUT_RETURN_CODE		= "INVALID_INPUT";


	protected int httpStatusCode;
	protected String returnCode;
	protected Object[] messageArgs;
	protected Object	data;

	/**
	 * 생성자
	 * @param httpStatusCode 응답 Http Status
	 * @param returnCode 응답 코드
	 * @param message 국제화 메세지
	 * @param cause 원인 익셉션(로그용)
	 * @param messageArgs 메세지 인수
	 */
	public CommonMessageException(int httpStatusCode, String returnCode, String message, Throwable cause, Object... messageArgs) {
		super(message, cause);
		this.httpStatusCode	= httpStatusCode;
		this.returnCode		= returnCode;
		this.messageArgs	= messageArgs;
	}

	/**
	 * 생성자
	 * @param httpStatusCode
	 * @param returnCode
	 * @param message
	 * @param messageArgs
	 */
	public CommonMessageException(int httpStatusCode, String returnCode, String message, Object... messageArgs) {
		this(httpStatusCode, returnCode, message, null, messageArgs);
	}

	/**
	 * 생성자
	 * @param httpStatusCode
	 * @param returnCode
	 * @param message
	 */
	public CommonMessageException(int httpStatusCode, String returnCode, String message) {
		this(httpStatusCode, returnCode, message, (Throwable)null, (Object[])null);
	}

	/**
	 * 생성자
	 * @param returnCode
	 * @param message
	 * @param cause
	 */
	public CommonMessageException(String returnCode, String message, Throwable cause) {
		this(DEFAULT_HTTP_STATUS_CODE, returnCode, message, cause, (Object[])null);
	}

	/**
	 * 생성자
	 * @param returnCode
	 * @param message
	 */
	public CommonMessageException(String returnCode, String message) {
		this(DEFAULT_HTTP_STATUS_CODE, returnCode, message, (Throwable)null, (Object[])null);
	}


	/**
	 * 생성자
	 * @param message
	 * @param cause
	 */
	public CommonMessageException(String message, Throwable cause, Object... msgArgs) {
		this(DEFAULT_HTTP_STATUS_CODE, DEFAULT_RETURN_CODE, message, cause, msgArgs);
	}

	/**
	 * 생성자
	 * @param message
	 * @param cause
	 */
	public CommonMessageException(String message, Throwable cause) {
		this(DEFAULT_HTTP_STATUS_CODE, DEFAULT_RETURN_CODE, message, cause, (Object[])null);
	}

	/**
	 * 생성자
	 * @param message
	 */
	public CommonMessageException(String message) {
		this(DEFAULT_HTTP_STATUS_CODE, DEFAULT_RETURN_CODE, message, (Throwable)null, (Object[])null);
	}
	
	public int getHttpStatusCode() {
		return this.httpStatusCode;
	}
	
	public String getReturnCode() {
		return this.returnCode;
	}
	
	public Object[] getMessageArgs() {
		return this.messageArgs;
	}

	@Override
	public String getLocalizedMessage() {
		return "[" + this.getHttpStatusCode() + "-" + this.getReturnCode() + "]" + this.getMessage();
	}

	/**
	 * 에러 응답에 포함할 데이터 객체를 얻습니다.
	 * @return
	 */
	public Object getData() {
		return data;
	}

	/**
	 * 에러 응답에 포함할 데이터 객체를 설정합니다.
	 * @param data
	 */
	public void setData(Object data) {
		this.data = data;
	}
}
