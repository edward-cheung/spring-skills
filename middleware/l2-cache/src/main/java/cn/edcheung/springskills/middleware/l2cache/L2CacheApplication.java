package cn.edcheung.springskills.middleware.l2cache;

import cn.edcheung.springskills.middleware.l2cache.annotation.L2Cacheable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class L2CacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(L2CacheApplication.class, args);
    }

    /**
     * 二级缓存实现思路：
     * 1. 自定义实现Cache、CacheManager
     * 2. 重写@Cacheable、@CacheEvict等Cache操作注解
     */
    @L2Cacheable(key = "'cache_test_' + #test", value = "testCache", cacheManager = "cacheManager")
    public String getTest(String test) {
        return test + 123;
    }

}
