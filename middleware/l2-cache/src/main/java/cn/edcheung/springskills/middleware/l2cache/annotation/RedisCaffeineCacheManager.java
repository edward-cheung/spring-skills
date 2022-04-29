package cn.edcheung.springskills.middleware.l2cache.annotation;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;

/**
 * Description RedisCaffeineCacheManager
 *
 * @author Edward Cheung
 * @date 2022/4/29
 * @since JDK 1.8
 */
public class RedisCaffeineCacheManager implements CacheManager {

    @Override
    public Cache getCache(String name) {
        return null;
    }

    @Override
    public Collection<String> getCacheNames() {
        return null;
    }
}
