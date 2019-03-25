package com.llmj.oss.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
@Configuration
@Slf4j(topic = "ossLogger")
public class RedisConfiguration {
	
	
	@Value("${spring.redis.host}")  
    private String host;  
    @Value("${spring.redis.port}")  
    private int port;  
    @Value("${spring.redis.timeout}") 
	private int timeout ;
    @Value("${spring.redis.password}")  
    private String password;  
    @Value("${spring.redis.database}")  
    private int database;  
    @Value("${spring.redis.pool.max-active}")  
    private int maxTotal;  
    @Value("${spring.redis.pool.max-idle}")  
    private int maxIdle;  
    @Value("${spring.redis.pool.max-wait}")  
    private int maxWaitMillis;  
  
    @Bean  
    public JedisPool JedisPool() {  
    	JedisPool pool = new JedisPool();
		// 池基本配置
		JedisPoolConfig config ;
		config = new JedisPoolConfig();
	  	config.setMaxTotal(maxTotal);// 最大连接数据库连接数 0无限制
		config.setMaxIdle(maxIdle);// 最大等待连接数 0无限制
		config.setMaxWaitMillis(maxWaitMillis);// 等待时间
		config.setTestOnBorrow(true);//在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；  
		pool = new JedisPool(config, host, port, timeout, password, database);
		Jedis jedis = pool.getResource();
		if (jedis != null) {
			log.info("JedisPool注入成功！！redis地址：" + host + ":" + port + " database:" + database);
			jedis.close();
		}
        return pool;  
    }  
    

}
