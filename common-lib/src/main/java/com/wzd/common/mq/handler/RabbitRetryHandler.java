package com.wzd.common.mq.handler;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.wzd.common.mq.retry.MQRetryHeader;
import com.wzd.common.mq.retry.MQRetryType;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
class RabbitRetryHandler implements RabbitListenerErrorHandler {

	@Value("${spring.rabbitmq.retry.count:2}")
	private int retryCnt; 
	
	@Value("${spring.rabbitmq.retry.delay:5000}")
	private int retryDelay; 
	
	@Value("${spring.rabbitmq.retry.fail.queue:fail}")
	private String failQueue; 
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry;
	
	@Override
	public Object handleError(Message amqpMessage, org.springframework.messaging.Message<?> message, ListenerExecutionFailedException exception) throws Exception {

		MessageProperties messageProperties = amqpMessage.getMessageProperties();
		String receivedExchange   = messageProperties.getReceivedExchange();
		String receivedRoutingKey = messageProperties.getReceivedRoutingKey();
		String routingKey = "";
		
		if (!receivedExchange.equals("")) {
			routingKey = receivedExchange;
		} else {
			routingKey = receivedRoutingKey;
		}
		
		Map<String, Object> headers = messageProperties.getHeaders();
		String queue = headers.get("QUEUE").toString();
		// Fail Queue 재명명
		failQueue = queue + ".fail";
		MQRetryType type = (MQRetryType)headers.get("TYPE");
		Integer retriesHeader = (Integer) headers.get(MQRetryHeader.X_RETRIES_HEADER);
		log.debug("routingKey=" + routingKey + ", queue=" + queue + ", type=" + type);
		
		if (!routingKey.equals("")) {
			if (type == MQRetryType.X_RETRY_BYPASS) {
				log.debug("MQRetryType.X_RETRY_BYPASS");
				rabbitTemplate.convertAndSend(failQueue, amqpMessage);
			} else if (type == MQRetryType.X_RETRY_BLOCKING) {
				log.debug("MQRetryType.X_RETRY_BLOCKING");
				stop(queue);
			} else if (type == MQRetryType.X_RETRY_RETRY) {
				log.debug("MQRetryType.X_RETRY_RETRY");
		        if (retriesHeader == null) {
		            retriesHeader = Integer.valueOf(0);
		        }
		        
	        	log.debug("retriesHeader=" + retriesHeader);

	        	if (retriesHeader < retryCnt) {
		        	headers.put(MQRetryHeader.X_RETRIES_HEADER, retriesHeader + 1);
		            headers.put(MQRetryHeader.X_DELAY_HEADER, Integer.valueOf(retryDelay));
//		            List<Map<String, ?>> xDeath = (List<Map<String, ?>>) headers.get(MQRetryHeader.X_DEATH_HEADER);
//		            List<String> routingKeys = (List<String>) xDeath.get(0).get("routing-keys");
//		            String exchange = (String) xDeath.get(0).get("exchange");
		            
		            if (!receivedExchange.equals("")) {
		    			rabbitTemplate.convertAndSend(receivedExchange, "", amqpMessage);
		    			TimeUnit.SECONDS.sleep(retryDelay);
		    		} else {
		    			rabbitTemplate.convertAndSend(receivedRoutingKey, amqpMessage);
		    			TimeUnit.SECONDS.sleep(retryDelay);
		    		}
		        } else {
		        	rabbitTemplate.convertAndSend(failQueue, amqpMessage);
		        }
		        
			} else if (type == MQRetryType.X_RETRY_ABORT) {
				log.debug("MQRetryType.X_RETRY_ABORT");
			} else {
				stop(queue);
				log.debug("MQRetryType.X_RETRY_BYPASS");
			}
		}
		
		throw new AmqpRejectAndDontRequeueException(exception);
	}
	
	private boolean isQueueListener(String queueName, MessageListenerContainer listenerContainer) {
        if (listenerContainer instanceof AbstractMessageListenerContainer) {
            AbstractMessageListenerContainer abstractMessageListenerContainer = (AbstractMessageListenerContainer) listenerContainer;
            String[] queueNames = abstractMessageListenerContainer.getQueueNames();
            return ArrayUtils.contains(queueNames, queueName);
        }
        return false;
    }
	
    public boolean stop(String queueName) {
        Collection<MessageListenerContainer> listenerContainers = this.rabbitListenerEndpointRegistry.getListenerContainers();
        for (MessageListenerContainer listenerContainer : listenerContainers) {
            if (this.isQueueListener(queueName, listenerContainer)) {
                listenerContainer.stop();
                return true;
            }
        }
        return false;
    }
    
    public boolean start(String queueName) {
        Collection<MessageListenerContainer> listenerContainers = this.rabbitListenerEndpointRegistry.getListenerContainers();
        for (MessageListenerContainer listenerContainer : listenerContainers) {
            if (this.isQueueListener(queueName, listenerContainer)) {
                listenerContainer.start();
                return true;
            }
        }
        return false;
    }

}