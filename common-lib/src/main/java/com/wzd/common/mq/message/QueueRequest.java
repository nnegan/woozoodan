package com.wzd.common.mq.message;

import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@ToString
public class QueueRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Map<String, Object> inRequestParams = new HashMap<String, Object>();

	public Object getInutParam(String key) {
		return inRequestParams.get(key);
	}

	public void setInputParam(String key, String value) {
		inRequestParams.put(key, value);
	}

}
