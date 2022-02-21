package cn.edcheung.springskills.middleware.l2cache.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Description RedisConfig
 *
 * @author zhangyi
 * @date 2020/10/22
 * @since JDK 1.8
 */
@EnableCaching
@Configuration
public class RedisConfig extends CachingConfigurerSupport {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(factory);
        StringRedisSerializer keySerializer = new StringRedisSerializer();

        // Jackson2JsonRedisSerializer和GenericJackson2JsonRedisSerializer的区别：
        // 1. 使用Jackson2JsonRedisSerializer反序列化带泛型数据类型会报错（已解决）
        // 2. 使用GenericJacksonRedisSerializer比Jackson2JsonRedisSerializer效率低，占用内存高
        // 3. 两者序列化LocalDateTime等类型时都有问题（已解决）

        // 设置CacheManager的值序列化方式为json序列化
        Jackson2JsonRedisSerializer<?> valueSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        // 解决查询缓存转换异常的问题
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        // 解决jackson2无法反序列化LocalDateTime的问题
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        //objectMapper.registerModule(new JavaTimeModule());
        valueSerializer.setObjectMapper(objectMapper);

        // 配置序列化（解决乱码的问题）
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(3L)) //设置缓存的默认超时时间
                .disableCachingNullValues() //如果是空值，不缓存
                .computePrefixWith(key -> key + ":") //覆盖默认的构造key，否则会多出一个冒号
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer)) //设置key序列化器
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer((valueSerializer))); //设置value序列化器

        return RedisCacheManager.builder(redisCacheWriter)
                .cacheDefaults(configuration)
                .transactionAware() //开启事务
                .build();
    }

    /**
     * 通过自定义CacheResolver实现动态选择CacheManager，例如热点缓存自动升降级的场景：
     * 自定义CacheResolver在调用resolveCaches选择CacheManager前，先判断此次读写的缓存key是否是热数据。
     * 如果是热数据则使用堆内存的CacheManager，否则使用redis的CacheManager。
     */
//    @Bean
    @Override
    public CacheResolver cacheResolver() {
        // 通过Guava实现的自定义堆内存缓存管理器
        CacheManager heapCacheManager = new ConcurrentMapCacheManager();
        CacheManager redisCacheManager = cacheManager();
        List<CacheManager> list = new ArrayList<>(2);
        // 优先读取堆内存缓存
        list.add(heapCacheManager);
        // 堆内存缓存读取不到该key时再读取redis缓存
        list.add(redisCacheManager);
        return new CustomCacheResolver(list);
    }

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new CustomKeyGenerator();
    }

    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        return new CustomErrorHandler();
    }

    /**
     * 重写Redis序列化方式，使用Json方式:
     * 当我们的数据存储到Redis的时候，我们的键（key）和值（value）都是通过Spring提供的Serializer序列化到数据库的。
     * RedisTemplate默认使用的是JdkSerializationRedisSerializer，StringRedisTemplate默认使用的是StringRedisSerializer。
     * Spring Data JPA为我们提供了下面的Serializer：
     * GenericToStringSerializer、Jackson2JsonRedisSerializer、JacksonJsonRedisSerializer、
     * JdkSerializationRedisSerializer、OxmSerializer、StringRedisSerializer。
     * 在此我们将自己配置RedisTemplate并定义Serializer。
     *
     * @param factory ConnectionFactory
     * @return RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 配置连接工厂
        template.setConnectionFactory(factory);

        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
        Jackson2JsonRedisSerializer<?> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        // 指定要序列化的域，field、get和set，以及修饰符范围，ANY是都有包括private和public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String、Integer等会抛出异常
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        // 值采用json序列化
        template.setValueSerializer(jackson2JsonRedisSerializer);

        // 设置hash key 和value序列化模式
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        //template.setHashValueSerializer(new FastJsonRedisSerializer<>(Object.class));

        template.afterPropertiesSet();
        return template;
    }
}
