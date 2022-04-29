package cn.edcheung.springskills.middleware.redissonapp;

import org.junit.jupiter.api.Test;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Description BloomFilterTests
 *
 * @author Edward Cheung
 * @date 2022/4/26
 * @since JDK 1.8
 */
@SpringBootTest
public class BloomFilterTests {

    @Autowired
    private RedissonClient client;

    @Test
    void contextLoads() {
    }

    @Test
    void test() throws InterruptedException {
        RBloomFilter<String> bloomFilter = client.getBloomFilter("bloomFilter");
        // 初始化布隆过滤器，预计统计元素数量为50000，期望误差率为0.01
        bloomFilter.tryInit(50000L, 0.01d);
        bloomFilter.add("field1Value");
        bloomFilter.add("field5Value");
        boolean res = bloomFilter.contains("field1Value");
        if (res) {
            System.out.println(" field1Value exists");
            // ...
        } else {
            System.out.println(" field1Value does not exist");
            // ...
        }
    }

}
