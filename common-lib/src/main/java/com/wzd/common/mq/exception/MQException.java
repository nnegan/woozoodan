package com.wzd.common.mq.exception;

public class MQException extends RuntimeException {

	private static final long serialVersionUID = -3929047274885451884L;

	public MQException(String message) {
    	super(message);
    }   	
	
	public MQException(String message, Throwable cause) {
    	super(message,cause);
    }   
}
