package com.wzd.common.mq.exception;


import com.wzd.common.mq.retry.MQRetryType;
import org.springframework.amqp.core.Message;

import java.util.Map;

public class MQRecvAbortException extends MQRecvException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 76744954662679163L;

	public MQRecvAbortException(String message) {
		super(message);  
	}
	public MQRecvAbortException(String message, Throwable cause) {
		super(message, cause);  
	}
	public MQRecvAbortException(String message, Message retryMessage, String queue) {
    	super(message);
    	Map<String, Object> headers = retryMessage.getMessageProperties().getHeaders();
    	headers.put("ERROR", message);
        headers.put("QUEUE", queue);
        headers.put("TYPE", MQRetryType.X_RETRY_ABORT);
    }
	
	public MQRecvAbortException(String message, Message retryMessage, String queue, Throwable cause) {
    	super(message, cause);
    	Map<String, Object> headers = retryMessage.getMessageProperties().getHeaders();
    	headers.put("ERROR", message);
        headers.put("QUEUE", queue);
        headers.put("TYPE", MQRetryType.X_RETRY_ABORT);
    }
}
