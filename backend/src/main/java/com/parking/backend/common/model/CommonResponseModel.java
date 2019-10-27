package com.parking.backend.common.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 응답 규격화 객체
 * @author pat
 *
 * @param <T>
 */
@SuppressWarnings("serial")
@JacksonXmlRootElement(localName = "response")
public class CommonResponseModel<T>
	implements Serializable {
	
	private String		returnCode;
	private String		message;
	private String[]	subMessages;
	private Integer		dataCount;
	private T			data;
	
	public String getReturnCode() {
		return returnCode;
	}
	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String[] getSubMessages() {
		return subMessages;
	}
	public void setSubMessages(String[] subMessages) {
		this.subMessages = subMessages;
	}
	public Integer getDataCount() {
		return dataCount;
	}
	public void setDataCount(Integer dataCount) {
		this.dataCount = dataCount;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "CommonResponseModel [returnCode=" + returnCode + ", message=" + message + ", subMessages="
				+ Arrays.toString(subMessages) + ", dataCount=" + dataCount + ", data=" + data + "]";
	}
}
