package cn.edcheung.springskills.middleware.zkapp.use;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description CuratorConfig
 *
 * @author Edward Cheung
 * @date 2022/3/18
 * @since JDK 1.8
 */
@Configuration
@ConfigurationProperties(prefix = "zookeeper.connect")
public class CuratorConfig {

    private String address;
    private int connectionTimeout;
    private int sessionTimeout;
    private int sleepTimeOut;
    private int maxRetries;
    private String namespace;

    /**
     * 初始化curator客户端
     *
     * @return
     */
    @Bean
    public CuratorFramework getCuratorClient() {
        // CuratorFrameworkFactory提供两个方法初始化curator连接：
        // 1. 一个是工厂方法newClient，使用工厂方法可以创建一个默认的curator实例
//        RetryPolicy retryPolicy = new ExponentialBackoffRetry(sleepTimeOut, maxRetries);
//        CuratorFramework client = CuratorFrameworkFactory.newClient(connectStr, retryPolicy);
//        client.start();
//        return client;
        // 2. 一个是构建方法build，build构建方法可以定制创建实例
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(sleepTimeOut, maxRetries);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(address)
                .connectionTimeoutMs(connectionTimeout)
                .sessionTimeoutMs(sessionTimeout)
                .namespace(namespace)
                .retryPolicy(retryPolicy).build();
        client.start();
        return client;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public int getSleepTimeOut() {
        return sleepTimeOut;
    }

    public void setSleepTimeOut(int sleepTimeOut) {
        this.sleepTimeOut = sleepTimeOut;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}

