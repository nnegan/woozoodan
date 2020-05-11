package com.wzd.common.mq;

import com.wzd.common.info.ServiceInfo;
import com.wzd.common.mq.model.MessageBaseModel;
import com.wzd.common.mq.model.MessageHeader;
import com.wzd.common.mq.exception.MQException;
import com.wzd.common.util.MDCUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Map;
import java.util.UUID;

/**
 * 
 * jhjo15, 20191219, key 변수 msgKey 로 변경 : SonarQube 소스 취약성 처리
 * 
 *  
 * Breeze
 * @since    : 2019/12/19
 *
 */
@Slf4j
@Component
public class MQUtils {

	@Autowired
	ServiceInfo serviceInfo;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
    public boolean mqSender(MessageBaseModel messageBaseModel, String type, String tag, String msgKey, String dest) throws Throwable {
        return this.mqSender(messageBaseModel, type, tag, msgKey, dest, (Map<String, Object>)null);
    }
	
	public boolean mqSender(MessageBaseModel messageBaseModel, String type, String tag, String msgKey, String dest, Map<String, Object> extraHeaders) throws Throwable {
		
		boolean isTrue = true;
		msgKey = "";
		
		try {
			
			if (type.equals("")) throw new MQException("mq type is needed");
			if (tag.equals("")) throw new MQException("mq tag is needed");
			
			String traceid = messageBaseModel.getTraceId();
			if (traceid==null||"".equals(traceid.trim())) 
				traceid = getTraceId();
			
			String messageBody = messageBaseModel.getMessage();
			
			MessageProperties props = MessagePropertiesBuilder.newInstance().setContentType(MessageProperties.DEFAULT_CONTENT_TYPE).build();
			Message message = new Message(messageBaseModel.getMessage().getBytes(), props);
			Map<String, Object> headers = message.getMessageProperties().getHeaders();
           

			headers.put(MessageHeader.TRACEID, traceid);
			headers.put(MessageHeader.TAG, tag);
			headers.put(MessageHeader.KEY, msgKey);

			headers.put(MessageHeader.SERVICE_SRC, serviceInfo.getServiceName());
			headers.put(MessageHeader.SERVICE_DEST, dest);
			
			 if (extraHeaders != null) {
	                headers.putAll(extraHeaders);
	         }
			
			log.debug(MessageHeader.TRACEID + " : {} ", traceid);
			log.debug(MessageHeader.SERVICE_SRC + " : {} ", serviceInfo.getServiceName());
			log.debug(MessageHeader.SERVICE_DEST + " : {} ", dest);
			log.debug("MSG_BODY : {}", messageBaseModel.toJsonString());
			
			if(type.equals(MessageHeader.MQ_QUEUE)) {
				rabbitTemplate.convertAndSend(tag, message);
			} else if (type.equals(MessageHeader.EX_DIRECT)) {
				rabbitTemplate.convertAndSend(tag, msgKey, message);
			} else if (type.equals(MessageHeader.EX_FANOUT)) {
				rabbitTemplate.convertAndSend(tag, msgKey, message);
			} else if (type.equals(MessageHeader.EX_TOPIC)) {
				rabbitTemplate.convertAndSend(tag, msgKey, message);
			} else {
				throw new MQException("Type invaldate... Check annotation type");
			}
		} catch (MQException ex) {
			log.error(ex.toString());
			isTrue = false;
		} catch (Exception ex) {
			log.error(ex.toString());
			isTrue = false;
		}
		
		return isTrue;
	}
	
    public boolean mqSender(String type, String tag, String msgKey, String dest, String traceid, String msg) throws Throwable {
        return this.mqSender(type, tag, msgKey, dest, traceid, msg, (Map<String, Object>)null);
    }

	public boolean mqSender(String type, String tag, String msgKey, String dest, String traceid, String msg, Map<String, Object> extraHeaders) throws Throwable {
		
		boolean isTrue = true;
		String newMsgKey = "";
		
		try {
			if (type.equals("")) throw new MQException("mq type is needed");
			if (tag.equals("")) throw new MQException("mq tag is needed");
			if (traceid==null||"".equals(traceid.trim())) {
				traceid = getTraceId();
			}
			
			MessageProperties props = MessagePropertiesBuilder.newInstance().setContentType(MessageProperties.DEFAULT_CONTENT_TYPE).build();
			Message message = new Message(msg.getBytes(), props);
			Map<String, Object> headers = message.getMessageProperties().getHeaders();
			
			headers.put(MessageHeader.TRACEID, traceid);
			headers.put(MessageHeader.TAG, tag);
			headers.put(MessageHeader.KEY, newMsgKey);

			headers.put(MessageHeader.SERVICE_SRC, serviceInfo.getServiceName());
			headers.put(MessageHeader.SERVICE_DEST, dest);
			
            if (extraHeaders != null) {
                headers.putAll(extraHeaders);
            }

			
			log.debug(MessageHeader.TRACEID + " : {} ", traceid);
			log.debug(MessageHeader.SERVICE_SRC + " : {} ", serviceInfo.getServiceName());
			log.debug(MessageHeader.SERVICE_DEST + " : {} ", dest);
			log.debug("MSG_BODY : {}", msg);
			
			if(type.equals(MessageHeader.MQ_QUEUE)) {
				rabbitTemplate.convertAndSend(tag, message);
			} else if (type.equals(MessageHeader.EX_DIRECT)) {
				rabbitTemplate.convertAndSend(tag, newMsgKey, message);
			} else if (type.equals(MessageHeader.EX_FANOUT)) {
				rabbitTemplate.convertAndSend(tag, newMsgKey, message);
			} else if (type.equals(MessageHeader.EX_TOPIC)) {
				rabbitTemplate.convertAndSend(tag, newMsgKey, message);
			} else {
				throw new MQException("Type invaldate... Check annotation type");
			}
		} catch (MQException ex) {
			log.error(ex.toString());
			isTrue = false;
		} catch (Exception ex) {
			log.error(ex.toString());
			isTrue = false;
		}
		
		return isTrue;
	}
	
    public boolean mqSender(String type, String tag, String msgKey, String msg) throws Throwable {
        return this.mqSender(type, tag, msgKey, msg, (Map<String, Object>)null);
    }

	public boolean mqSender(String type, String tag, String msgKey, String msg, Map<String, Object> extraHeaders) throws Throwable {
		
		boolean isTrue = true;
		String newMsgKey = "";
		
		try {
			
			if (type.equals("")) throw new MQException("mq type is needed");
			if (tag.equals("")) throw new MQException("mq tag is needed");
			String traceid = getTraceId();
			MessageProperties props = MessagePropertiesBuilder.newInstance().setContentType(MessageProperties.DEFAULT_CONTENT_TYPE).build();
			Message message = new Message(msg.getBytes(), props);
			Map<String, Object> headers = message.getMessageProperties().getHeaders();
			
			headers.put(MessageHeader.TAG, tag);
			headers.put(MessageHeader.KEY, newMsgKey);
			headers.put(MessageHeader.TRACEID, traceid);
			headers.put(MessageHeader.SERVICE_SRC, serviceInfo.getServiceName());
            if (extraHeaders != null) {
                headers.putAll(extraHeaders);
            }

			log.debug(MessageHeader.SERVICE_SRC + " : {} ", serviceInfo.getServiceName());
			log.debug("MSG_BODY : {}", msg);
			
			if(type.equals(MessageHeader.MQ_QUEUE)) {
				rabbitTemplate.convertAndSend(tag, message);
			} else if (type.equals(MessageHeader.EX_DIRECT)) {
				rabbitTemplate.convertAndSend(tag, newMsgKey, message);
			} else if (type.equals(MessageHeader.EX_FANOUT)) {
				rabbitTemplate.convertAndSend(tag, newMsgKey, message);
			} else if (type.equals(MessageHeader.EX_TOPIC)) {
				rabbitTemplate.convertAndSend(tag, newMsgKey, message);
			} else {
				throw new MQException("Type invaldate... Check annotation type");
			}
		} catch (MQException ex) {
			log.error(ex.toString());
			isTrue = false;
		} catch (Exception ex) {
			log.error(ex.toString());
			isTrue = false;
		}
		
		return isTrue;
	}
	
	private String getTraceId() {
		try {
			String traceid = MDCUtils.get(MDCUtils.TRACE_ID);
			//			String traceid = messageBaseModel.getTraceId();
			if (traceid==null||"".equals(traceid.trim())) {
				traceid = UUID.randomUUID().toString();
				MDCUtils.set(MDCUtils.TRACE_ID, traceid);
			}
			return traceid;
		}
		catch (Exception ex) {
			log.error(ex.toString());
		}
		return "";
		
	}
	public void setMqTraceId(Message message) {
		try {
			
			if(message!=null) {
				Map<String, Object> headers = message.getMessageProperties().getHeaders();
				String traceId = (String)headers.get("X-Trace-Id");
				if (traceId==null || "".equals(headers.get("X-Trace-Id"))) {
					traceId = UUID.randomUUID().toString();
				}

				MDCUtils.set(MDCUtils.SERVICE_NAME, serviceInfo.getServiceName());
				MDCUtils.set(MDCUtils.TRACE_ID, traceId);
			}
	
		}catch (Exception ex) {
			log.error("Exception" + ex.toString());
		}
	}
}
