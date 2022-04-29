package cn.edcheung.springskills.middleware.zookeeperapp.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * Description ZookeeperConfig
 *
 * @author Edward Cheung
 * @date 2022/3/18
 * @since JDK 1.8
 */
@Configuration
@PropertySource("classpath:zookeeper.properties")
public class ZookeeperConfig {

    @Autowired
    private Environment environment;

    @Bean(initMethod = "init", destroyMethod = "destroy")
    public ZookeeperClient zookeeperClient() {
        String zookeeperServer = environment.getRequiredProperty("zookeeper.server");
        String zookeeperLockPath = environment.getRequiredProperty("zookeeper.lockPath");
        return new ZookeeperClient(zookeeperServer, zookeeperLockPath);
    }

}
