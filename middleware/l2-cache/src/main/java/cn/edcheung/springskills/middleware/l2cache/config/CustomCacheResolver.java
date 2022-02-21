package cn.edcheung.springskills.middleware.l2cache.config;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Description CustomCacheResolver
 *
 * @author zhangyi
 * @date 2020/11/27
 * @since JDK 1.8
 */
public class CustomCacheResolver implements CacheResolver {

    private List<CacheManager> cacheManagerList;

    public CustomCacheResolver() {
    }

    public CustomCacheResolver(List<CacheManager> cacheManagerList) {
        this.cacheManagerList = cacheManagerList;
    }

    public List<CacheManager> getCacheManagerList() {
        return cacheManagerList;
    }

    public void setCacheManagerList(List<CacheManager> cacheManagerList) {
        this.cacheManagerList = cacheManagerList;
    }

    @Override
    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
        Collection<String> cacheNames = getCacheNames(context);
        Collection<Cache> result = new ArrayList<>(2);
        for (CacheManager cacheManager : getCacheManagerList()) {
            for (String cacheName : cacheNames) {
                Cache cache = cacheManager.getCache(cacheName);
                if (cache == null) {
                    throw new IllegalArgumentException("Cannot find cache named '" + cacheName
                            + "' for " + context.getOperation());
                }
                result.add(cache);
            }
        }
        return result;
    }

    private Collection<String> getCacheNames(CacheOperationInvocationContext<?> context) {
        return context.getOperation().getCacheNames();
    }
}
