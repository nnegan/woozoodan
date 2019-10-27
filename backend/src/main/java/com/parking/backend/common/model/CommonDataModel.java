package com.parking.backend.common.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.io.Serializable;

/**
 * 범용 단일 응답용 모델 
 * @author pat
 */
@SuppressWarnings("serial")
@JacksonXmlRootElement(localName = "response")
public class CommonDataModel<T>  implements Serializable {
	
	private T data;

	
	public CommonDataModel() {
		this(null);
	}
	
	public CommonDataModel(T data) {
		this.data	= data;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "CommonDataModel [data=" + data + "]";
	}
	
}