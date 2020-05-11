package com.wzd.common.mq.exception;

import com.wzd.common.mq.retry.MQRetryType;
import org.springframework.amqp.core.Message;

import java.util.Map;

public class MQRecvBypassException extends MQRecvException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4767925004954262603L;

	public MQRecvBypassException(String message) {
		super(message);  
	}
	public MQRecvBypassException(String message, Throwable cause) {
		super(message, cause);  
	}
	public MQRecvBypassException(String message, Message retryMessage, String queue) {
    	super(message);
    	Map<String, Object> headers = retryMessage.getMessageProperties().getHeaders();
    	headers.put("ERROR", message);
        headers.put("QUEUE", queue);
        headers.put("TYPE", MQRetryType.X_RETRY_BYPASS);
    }
	
	public MQRecvBypassException(String message, Message retryMessage, String queue, Throwable cause) {
    	super(message, cause);
    	Map<String, Object> headers = retryMessage.getMessageProperties().getHeaders();
    	headers.put("ERROR", message);
        headers.put("QUEUE", queue);
        headers.put("TYPE", MQRetryType.X_RETRY_BYPASS);
    }
}
