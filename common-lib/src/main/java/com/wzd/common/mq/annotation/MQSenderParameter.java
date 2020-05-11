package com.wzd.common.mq.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface MQSenderParameter {

	String type() default "";
	
	String tag() default "";
	
	String key() default "";
	
	String dest() default "";
	
}
