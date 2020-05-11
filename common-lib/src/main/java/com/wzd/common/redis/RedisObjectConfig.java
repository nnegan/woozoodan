package com.wzd.common.redis;

import com.wzd.common.redis.properties.RedisObjectProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis - Object용으로 환경을 구성한다.
 *
 * @author suyang
 * @version 1.0
 * @since 2018. 1.
 */

@Slf4j
@Configuration
@Profile({ "local" })
public class RedisObjectConfig {


	@Autowired
	protected RedisObjectProperties redisObjectProperties;

	/**
	 * Redis 연결용 Connection Pooling 설정.
	 *
	 * @return JedisPoolConfig 설정
	 */
	@Bean(name="objectJedisPoolConfig")
	@Qualifier("objectJedisPoolConfig")
	public JedisPoolConfig jedisPoolConfig() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
//        jedisPoolConfig.setMaxIdle(maxPooling);
//        jedisPoolConfig.setMinIdle(minPooling);
		jedisPoolConfig.setTestOnBorrow(true);
		jedisPoolConfig.setTestOnReturn(true);
		return jedisPoolConfig;
	}

	/**
	 * Redis 연결용 ConnectionFactory를 얻는다.
	 *
	 * @return JedisConnectionFactory Redis 연결용 ConnectionFactory
	 */
	@Bean(name = "redisObjectConnectionFactory")
//	@Profile("awsredis")
	@Qualifier("redisObjectConnectionFactory")
	public JedisConnectionFactory jedisConnectionFactory() {
		if (redisObjectProperties.getCluster() == null) {
			log.debug("RedisManager Object - redisClientType : NONE");
			JedisConnectionFactory factory = new JedisConnectionFactory(jedisPoolConfig());
			factory.setHostName(redisObjectProperties.getHost());
			factory.setPort(redisObjectProperties.getPort());
			factory.setDatabase(redisObjectProperties.getDatabase());
			//YML에 'shared-object' 항목이 설정되어 있지 않을 경우 getTimeout() 값은 null로 넘어옴. host와 port는 기본값인 localhost와 6379가 설정됨.
			if (redisObjectProperties.getTimeout()!=null)
				factory.setTimeout((int)(redisObjectProperties.getTimeout().toMillis() * 1000)); // milliseconds이므로 seconds로변환
			factory.setUsePool(true);
			factory.afterPropertiesSet();
			return factory;
		} else {
			log.debug("RedisManager Object - redisClientType : CLUSTER");
			JedisConnectionFactory factory = new JedisConnectionFactory(new RedisClusterConfiguration(redisObjectProperties.getCluster().getNodes()));
			//factory.setDatabase(redisObjectProperties.getDatabase());
			factory.setTimeout((int)(redisObjectProperties.getTimeout().toMillis() * 1000)); // milliseconds이므로 seconds로변환
			return factory;

		}
	}

	/**
	 * Redis Object사용을 위한 RedisTemplate를 얻는다.
	 *
	 * @return Redis Object사용을 위한 RedisTemplate
	 */
	@Bean(name = "redisObjectTemplate")
	@Qualifier("redisObjectTemplate")
//	@Profile("awsredis")
	public RedisTemplate redisObjectTemplate() {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
		RedisSerializer<String> stringSerializer = new StringRedisSerializer();
		redisTemplate.setKeySerializer(stringSerializer);
		redisTemplate.setValueSerializer(stringSerializer);
		redisTemplate.setHashKeySerializer(stringSerializer);
		redisTemplate.setHashValueSerializer(stringSerializer);
		redisTemplate.setDefaultSerializer(stringSerializer);
		redisTemplate.setConnectionFactory(this.jedisConnectionFactory());
		return redisTemplate;
	}
}
