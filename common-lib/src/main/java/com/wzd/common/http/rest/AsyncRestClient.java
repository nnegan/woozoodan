package com.wzd.common.http.rest;

import com.wzd.common.util.JsonUtils;
import com.wzd.common.util.MDCUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.*;
import org.springframework.http.client.AsyncClientHttpRequest;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.net.URI;

/**
 * AsyncRestTemplate
 */

@Slf4j
@SuppressWarnings("deprecation")
public class AsyncRestClient extends AsyncRestTemplate {
	@Autowired
	private ConfigurableEnvironment environment;

	public AsyncRestClient() {
		super();
	}

	public AsyncRestClient(AsyncClientHttpRequestFactory requestFactory) {
		super();
		setAsyncRequestFactory(requestFactory);
	}

	@Override
	protected AsyncClientHttpRequest createAsyncRequest(URI url, HttpMethod method) throws IOException {
		AsyncClientHttpRequest request = super.createAsyncRequest(url, method);

		String urlPath = url.getPath();
		String service = "";
		if (urlPath != null) {
			service = urlPath.substring(urlPath.lastIndexOf("/") + 1, urlPath.length());
		}

		String appSource = environment.getProperty("context-path","default");
		long serviceTime = System.currentTimeMillis();

		request.getHeaders().add("service", service);
		request.getHeaders().add("serviceTime", Long.toString(serviceTime));
		request.getHeaders().add("appSource", appSource);

		String traceId = MDCUtils.get(MDCUtils.TRACE_ID);
		request.getHeaders().add("X-Trace-Id", traceId);

		log.debug("@# Created asynchronous traceid: {}, method name: {}, request for: {}", traceId, method.name(), url);

		return request;
	}

	public <T> ListenableFuture<ResponseEntity<T>> postForEntity(String url, Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(JsonUtils.toJson(request), headers);

		return super.postForEntity(url, entity, responseType, uriVariables);
	}
}
