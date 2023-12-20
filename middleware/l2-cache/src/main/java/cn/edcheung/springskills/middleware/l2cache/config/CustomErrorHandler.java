package cn.edcheung.springskills.middleware.l2cache.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

/**
 * Description 自应用 spring cache 操作异常处理器
 * <p>
 * 缓存读写发生了异常，如果是读取redis异常，会导致读取缓存的值为空，从而继续从mysql读取数据，对业务没有影响。
 * 但是如果请求量很大就会出现缓存雪崩的问题，大量的查询请求发送到mysql导致mysql负载过大而阻塞甚至宕机，建议使用多层缓存兜底。
 * <p>
 * 如果缓存写发生了异常，就可能导致mysql的数据和redis缓存的数据不一致的问题。
 * 为了解决该问题，需要继续扩展CacheErrorHandler的handleCachePutError和handleCacheEvictError方法，
 * 思路就是将redis写操作失败的key保存下来，通过重试任务删除这些key对应的redis缓存解决mysql数据与redis缓存数据不一致的问题。
 *
 * @author Edward Cheung
 * @date 2020/11/24
 * @since JDK 1.8
 */
public class CustomErrorHandler implements CacheErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomErrorHandler.class);

    @Override
    public void handleCacheGetError(RuntimeException e, Cache cache, Object o) {
        logger.error("读取缓存失败" + cache.getName(), e);
    }

    @Override
    public void handleCachePutError(RuntimeException e, Cache cache, Object o, Object o1) {
        logger.error("写入缓存失败" + cache.getName(), e);
    }

    @Override
    public void handleCacheEvictError(RuntimeException e, Cache cache, Object o) {
        logger.error("删除缓存失败" + cache.getName(), e);
    }

    @Override
    public void handleCacheClearError(RuntimeException e, Cache cache) {
        logger.error("清空缓存失败" + cache.getName(), e);
    }
}
