package com.wzd.common.mq.exception;


import com.wzd.common.mq.retry.MQRetryType;
import org.springframework.amqp.core.Message;

import java.util.Map;

public class MQRecvBlockingException extends MQRecvException {


	private static final long serialVersionUID = 3107343173707468207L;

	public MQRecvBlockingException(String message) {
		super(message);  
	}
	
	public MQRecvBlockingException(String message, Throwable cause) {
		super(message, cause);  
	}
	
	public MQRecvBlockingException(String message, Message retryMessage, String queue) {
    	super(message);
    	Map<String, Object> headers = retryMessage.getMessageProperties().getHeaders();
    	headers.put("ERROR", message);
        headers.put("QUEUE", queue);
        headers.put("TYPE", MQRetryType.X_RETRY_BLOCKING);
    }
	
	public MQRecvBlockingException(String message, Message retryMessage, String queue, Throwable cause) {
    	super(message, cause);
    	Map<String, Object> headers = retryMessage.getMessageProperties().getHeaders();
    	headers.put("ERROR", message);
        headers.put("QUEUE", queue);
        headers.put("TYPE", MQRetryType.X_RETRY_BLOCKING);
    }
}
