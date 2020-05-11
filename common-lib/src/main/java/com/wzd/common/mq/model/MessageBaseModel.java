package com.wzd.common.mq.model;

import lombok.Getter;
import lombok.Setter;

public abstract class MessageBaseModel implements MessageBase {
		
	@Getter
    @Setter
	public String traceId;
	
	@Getter
    @Setter
	public String message;
	
	@Override
	public String toJsonString() {
		// TODO Auto-generated method stub
		return "";
	}

}
