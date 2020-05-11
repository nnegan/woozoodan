package com.wzd.common.mq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.wzd.common.http.rest.AsyncRestClient;
import com.wzd.common.http.rest.RestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
public abstract class DefaultServiceInterfaceImpl implements QueueServiceInterface {

	@Autowired
	public AsyncRestClient restAsyncClient;

	@Autowired
	public RestClient restClient;

	@Autowired
	protected ConnectionFactory connectionFactory;

	@Value("${spring.rabbitmq.retry.delay:30}")
	private long retrydelay;

	private Channel channel;
	
	@PostConstruct
	private void init() {
		channel = connectionFactory.createConnection().createChannel(false);
	}
	
	@Override
	public ResponseEntity syncCall(Object request)
			throws InterruptedException, ExecutionException, UnsupportedEncodingException {

		return invokeSyncCall(request);
	}

	@Override
	public ResponseEntity asyncCall(Object request)
			throws InterruptedException, ExecutionException, UnsupportedEncodingException {

		// purposely dropping out the response
		Future<ResponseEntity> responseFuture = invokeAsyncCall(request);
		ResponseEntity gatewayResponse = responseFuture.get();
		return gatewayResponse;
	}

	private ResponseEntity invokeSyncCall(Object request)
			throws InterruptedException, ExecutionException, UnsupportedEncodingException {
		return invokeTarget(request);
	}

	@Async
	private Future<ResponseEntity> invokeAsyncCall(Object request)
			throws InterruptedException, ExecutionException, UnsupportedEncodingException {
		ResponseEntity response = invokeTarget(request);

		return new AsyncResult<ResponseEntity>(response);
	}

	public abstract ResponseEntity invokeTarget(Object request)
			throws InterruptedException, ExecutionException, UnsupportedEncodingException;

	public void sendMessage(String exchange, String binding, Object request) throws InterruptedException  {

		AMQP.BasicProperties.Builder props = new AMQP.BasicProperties.Builder();
		Map<String, Object> headers = new HashMap<>();
		headers.put("x-delay", retrydelay);
		props.headers(headers);
		props.deliveryMode(2);
		
		try {
			channel.basicPublish(exchange, binding, true, props.build(), (byte[]) request);
		}catch(Exception e) {
			log.debug(e.getMessage());;
			throw new InterruptedException();
		}	
	}

	public void exLogging(String method, Exception e) {
		log.debug("%s.%s => %s \n 발생원인 : %s", this.getClass().getName(), method, e.getClass().getName(),
				e.getMessage());
	}
}