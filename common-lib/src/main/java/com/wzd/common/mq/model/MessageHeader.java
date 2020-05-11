package com.wzd.common.mq.model;

public final class MessageHeader {
	
	public static final String TAG        = "X-Tag";
	public static final String TAG_ACTION = "X-Tag-Action";
	public static final String KEY        = "X-Key";
	
	public static final String TRACEID    = "X-Trace-Id";
	public static final String ACTION     = "X-Action";

	public static final String SERVICE_SRC  = "X-Service-Src";
	public static final String SERVICE_DEST = "X-Service-Dest";
	
	public static final String MQ_QUEUE  = "MQ_QUEUE";
	public static final String EX_DIRECT = "EX_DIRECT";
	public static final String EX_TOPIC  = "EX_TOPIC";
	public static final String EX_FANOUT = "EX_FANOUT";
	
	public static final String MQ_SENDER = "MQ_SENDER";
	public static final String MQ_RECVER = "MQ_RECVER";
	
	public static final String KAFKA_SENDER = "KAFKA_SENDER";
	
	public static final String KAFKA = "KAFKA";
	public static final String MQ    = "MQ";
	
}
