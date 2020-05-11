package com.wzd.common.mq;

public interface RabbitListener {

	public static final String X_DEATH_HEADER   = "x-death";
	public static final String X_DELAY_HEADER   = "x-delay";
	public static final String X_RETRIES_HEADER = "x-retries";
    
	public static final String DISCARD  = "discard";
	public static final String BYPASS   = "bypass";
	public static final String RETRY    = "retry";
	public static final String BLOCKING = "blocking";

}
