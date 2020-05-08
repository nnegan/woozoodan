package com.wzd.common.model;

import java.io.Serializable;

/**
 * BackendProxy 구성을 위한 정보를 받을때 사용하는 모델
 * @author pat
 *
 */
@SuppressWarnings("serial")
public class ProxyRouteInfoModel
	implements Serializable {

	// 요청 URL
	private String	requestUri;
	
	// 전달할 URL
	private String	originUri;
	
	// 최종 수정 시각
	private long	modifiedTime;

	public String getRequestUri() {
		return requestUri;
	}

	public void setRequestUri(String requestUri) {
		this.requestUri = requestUri;
	}

	public String getOriginUri() {
		return originUri;
	}

	public void setOriginUri(String originUri) {
		this.originUri = originUri;
	}

	public long getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	@Override
	public String toString() {
		return "ProxyRouteInfoModel [requestUri=" + requestUri + ", originUri=" + originUri
				+ ", modifiedTime=" + modifiedTime + "]";
	}
}
