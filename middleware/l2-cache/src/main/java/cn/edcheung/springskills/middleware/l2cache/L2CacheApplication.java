package cn.edcheung.springskills.middleware.l2cache;

import cn.edcheung.springskills.middleware.l2cache.annotation.L2Cacheable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class L2CacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(L2CacheApplication.class, args);
    }

    @L2Cacheable()
    public String getTest(String id) {
        return id + 123;
    }

}
