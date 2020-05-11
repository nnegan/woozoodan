package com.wzd.common.mq.message;

import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@ToString
public class QueueResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private Object response;

	private final Map<String, Object> outRequestParams = new HashMap<String, Object>();

	public void setOutputParam(String key, Object value) {
		outRequestParams.put(key, value);
	}

	public Object getOutputParam(String key) {
		return outRequestParams.get(key);
	}

	public Object getResponse() {
		return response;
	}

	public void setResponse(Object response) {
		this.response = response;
	}
}