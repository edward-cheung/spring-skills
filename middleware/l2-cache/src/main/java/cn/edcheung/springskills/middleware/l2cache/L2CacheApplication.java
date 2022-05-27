package cn.edcheung.springskills.middleware.l2cache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.Cacheable;

@SpringBootApplication
public class L2CacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(L2CacheApplication.class, args);
    }

    @Cacheable(key = "'cache_test_' + #test", value = "testCache", cacheManager = "cacheManager")
    public String getTest(String test) {
        return test + 123;
    }

}
