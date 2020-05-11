package com.wzd.common.mq.exception;

import com.wzd.common.mq.retry.MQRetryType;
import org.springframework.amqp.core.Message;

import java.util.Map;

public class MQRecvRetryException extends MQRecvException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1522001157050893351L;

	public MQRecvRetryException(String message) {
		super(message);  
	}
	public MQRecvRetryException(String message, Throwable cause) {
		super(message, cause);  
	}
	public MQRecvRetryException(String message, Message retryMessage, String queue) {
    	super(message);
    	Map<String, Object> headers = retryMessage.getMessageProperties().getHeaders();
    	headers.put("ERROR", message);
        headers.put("QUEUE", queue);
        headers.put("TYPE", MQRetryType.X_RETRY_RETRY);
    }
	public MQRecvRetryException(String message, Message retryMessage, String queue, Throwable cause) {
    	super(message,cause);
    	Map<String, Object> headers = retryMessage.getMessageProperties().getHeaders();
    	headers.put("ERROR", message);
        headers.put("QUEUE", queue);
        headers.put("TYPE", MQRetryType.X_RETRY_RETRY);
    }
}
