package com.wzd.common.mq;

import com.wzd.common.mq.annotation.MQSenderParameter;
import com.wzd.common.mq.annotation.MQSenderReturn;
import com.wzd.common.mq.exception.MQException;
import com.wzd.common.mq.model.MessageBase;
import com.wzd.common.mq.model.MessageBaseModel;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class MQMessageAspect {
	
	@Autowired
	MQUtils mqUtils;
	
	@Before(value = "@annotation(com.wzd.common.mq.annotation.MQSenderParameter) && @ annotation(mqSenderParameter)")
	public void mqAspectBefore(JoinPoint pjp, MQSenderParameter mqSenderParameter) throws Throwable {
		
		try {
			if(pjp.getArgs().length == 0) 
				throw new MQException("Message Check : Parameter is length zeno");
			if(pjp.getArgs()[0] instanceof MessageBase) {
				MessageBaseModel messageBaseModel = (MessageBaseModel)pjp.getArgs()[0];
				
				String type = mqSenderParameter.type();						
				String tag  = mqSenderParameter.tag();
				String key  = mqSenderParameter.key();
				String dest = mqSenderParameter.dest();
				
				mqUtils.mqSender(messageBaseModel, type, tag, key, dest);
			} else {
				throw new MQException("Message Check : Massege type -> Message Parameter 0");
			}
			
		} catch (MQException ex) {
			log.error(ex.toString());
		} catch (Exception ex) {
			log.error(ex.toString());
		}
	}
	
	@AfterReturning(value = "@annotation(com.wzd.common.mq.annotation.MQSenderReturn) && @ annotation(mqSenderReturn)", returning = "resultMessage")
	public void mqAspectReturn(JoinPoint pjp, MQSenderReturn mqSenderReturn, Object resultMessage) throws Throwable {
		
		try {
			if(resultMessage instanceof MessageBase) {
				MessageBaseModel messageBaseModel = (MessageBaseModel)resultMessage;

				String type = mqSenderReturn.type();						
				String tag  = mqSenderReturn.tag();
				String key  = mqSenderReturn.key();
				String dest = mqSenderReturn.dest();
				
				mqUtils.mqSender(messageBaseModel, type, tag, key, dest);
			} else {
				throw new MQException("Message Check : Massege type -> Message return type");
			}
			
		} catch (MQException ex) {
			log.error(ex.toString());
		} catch (Exception ex) {
			log.error(ex.toString());
		}
	}
	
	@Before(value = "@annotation(org.springframework.amqp.rabbit.annotation.RabbitListener)")
	public void beforeTransaction(JoinPoint joinPoint) {
	    for (Object arg : joinPoint.getArgs()) {
	        if (arg instanceof Message) {
				try {
					mqUtils.setMqTraceId((Message)arg);
				}
				catch (Exception ex) {
					log.error(ex.toString());
				}
	        }
	    }
	}
}
