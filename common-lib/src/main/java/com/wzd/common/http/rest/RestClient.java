package com.wzd.common.http.rest;


import com.wzd.common.util.MDCUtils;
import com.wzd.common.util.QueryStringBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * RestTemplate
 */

@Slf4j
@SuppressWarnings("deprecation")
public class RestClient extends RestTemplate {

	@Autowired
	private ConfigurableEnvironment environment;

	public RestClient() {
		super();
	}

	/**
	 * Create a new instance of the {@link RestTemplate} based on the given
	 * {@link ClientHttpRequestFactory}.
	 * 
	 * @param requestFactory
	 *            HTTP request factory to use
	 * @see org.springframework.http.client.SimpleClientHttpRequestFactory
	 * @see org.springframework.http.client.HttpComponentsClientHttpRequestFactory
	 */
	public RestClient(ClientHttpRequestFactory requestFactory) {
		super(requestFactory);
	}

	/**
	 * Create a new instance of the {@link RestTemplate} using the given list of
	 * {@link HttpMessageConverter} to use
	 * 
	 * @param messageConverters the list of {@link HttpMessageConverter} to use
	 * @since 3.2.7
	 */
	public RestClient(List<HttpMessageConverter<?>> messageConverters) {
		super(messageConverters);
	}

	public <T> T getForObject(String url, String path, Object object, Class<T> responseType) throws RestClientException {
		QueryStringBuilder queryStringBuilder = new QueryStringBuilder();
		UriComponentsBuilder uriComponent = queryStringBuilder.create(object, url, path);
		return super.getForObject(uriComponent.build().encode().toUri(), responseType);
	}

	@Override
	public <T> T postForObject(String url, Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
		return super.postForObject(url, request, responseType, uriVariables);
	}
	
	@Override
	protected ClientHttpRequest createRequest(URI url, HttpMethod method) throws IOException {

		ClientHttpRequest clientHttpRequest = super.createRequest(url, method);
		String urlPath = url.getPath();
		String service = "";
		if (urlPath != null) {
			service = urlPath.substring(urlPath.lastIndexOf("/") + 1, urlPath.length());
		}
		String appSource = environment.getProperty("server.context-path","default");
		long serviceTime = System.currentTimeMillis();

		clientHttpRequest.getHeaders().add("service", service);
		clientHttpRequest.getHeaders().add("serviceTime", Long.toString(serviceTime));
		clientHttpRequest.getHeaders().add("appSource", appSource);

		String traceId = MDCUtils.get(MDCUtils.TRACE_ID);
		clientHttpRequest.getHeaders().add("X-Trace-Id", traceId);
		log.debug("@# Created synchronous traceid: {}, method name: {}, request for: {}", traceId, method.name(), url);

		return clientHttpRequest;
	}

}
