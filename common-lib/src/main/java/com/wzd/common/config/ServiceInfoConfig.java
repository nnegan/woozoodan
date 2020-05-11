package com.wzd.common.config;

import com.wzd.common.info.ServiceInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceInfoConfig {

	@Value("${spring.application.name:wzd-test}")
	private String serviceName;
	
	@Bean(name = "getServiceInfo")
	public ServiceInfo getServiceInfo() {
		ServiceInfo serviceInfo = new ServiceInfo(); 
		serviceInfo.setServiceName(serviceName);
		return serviceInfo;
	}
}
