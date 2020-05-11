package com.wzd.common.mq.exception;

import com.wzd.common.mq.retry.MQRetryType;
import org.springframework.amqp.core.Message;

import java.util.Map;

public class MQRecvException extends MQException {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -6664811950124872035L;

	public MQRecvException(String message) {
    	super(message);    
    }
	public MQRecvException(String message, Throwable cause) {
    	super(message, cause);    
    }
	
    public MQRecvException(String message, Message retryMessage, String queue, MQRetryType type) {
    	super(message);
    	Map<String, Object> headers = retryMessage.getMessageProperties().getHeaders();
    	headers.put("ERROR", message);
    	headers.put("QUEUE", queue);
		headers.put("TYPE", type);
    }
    
    public MQRecvException(String message, Message retryMessage, String queue, MQRetryType type, Throwable cause) {
    	super(message, cause);
    	Map<String, Object> headers = retryMessage.getMessageProperties().getHeaders();
    	headers.put("ERROR", message);
    	headers.put("QUEUE", queue);
		headers.put("TYPE", type);
    }
}
