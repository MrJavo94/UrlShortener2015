package urlshortener2015.imperialred.config;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
	
	/* Time in seconds before the cache contents expire */
	private static final long CACHE_EXPIRE_TIME = 300;
	
	private static final Logger logger = LoggerFactory
			.getLogger(CacheConfig.class);
	
	@Bean
    public CacheManager cacheManager() {
		SimpleCacheManager cacheManager = new SimpleCacheManager();
		cacheManager.setCaches(Arrays.asList(new ConcurrentMapCache("shortUrl"),
				new ConcurrentMapCache("users")));
        return cacheManager;
    }
	
	/* Redis cache (not working) */
	
	/*@Bean
	public JedisConnectionFactory redisConnectionFactory() {
		JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory();
		try {
			//redisConnectionFactory.setHostName(InetAddress.getLocalHost().getHostName());
			redisConnectionFactory.setHostName("localhost");
			logger.info("OST: " + InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		redisConnectionFactory.setPort(8090);
		redisConnectionFactory.setUsePool(true);
		return redisConnectionFactory;
	}
	
	@Bean
	public RedisTemplate<String,String> redisTemplate(RedisConnectionFactory cf) {
		RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
		redisTemplate.setConnectionFactory(cf);
		return redisTemplate;
	}
	
	@Bean
	public CacheManager cacheManager(RedisTemplate<String, String> redisTemplate) {
		RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
		cacheManager.setDefaultExpiration(CACHE_EXPIRE_TIME);
		return cacheManager;
	}*/
	
	/* Attempt to permit multithreading in Redis cache */
	
	/*@Bean
	protected ThreadPoolTaskExecutor mvcTaskExecutor() {
	    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	    executor.setThreadNamePrefix("my-mvc-task-executor-");
	    executor.setCorePoolSize(5);
	    executor.setMaxPoolSize(200);
	    return executor;
	}

	@Bean
	protected WebMvcConfigurer webMvcConfigurer() {
	    return new WebMvcConfigurerAdapter() {
	        @Override
	        public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
	            configurer.setTaskExecutor(mvcTaskExecutor());
	        }
	    };
	}*/
	
}
