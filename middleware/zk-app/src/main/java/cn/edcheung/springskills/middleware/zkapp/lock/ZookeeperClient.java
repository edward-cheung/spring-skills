package cn.edcheung.springskills.middleware.zkapp.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description ZookeeperClient
 *
 * @author Edward Cheung
 * @date 2022/3/18
 * @since JDK 1.8
 */
public class ZookeeperClient {

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperClient.class);

    private static final int SLEEP_TIME_MS = 1000;
    private static final int MAX_RETRIES = 3;

    private String zookeeperServer;
    private String zookeeperLockPath;

    private CuratorFramework client;

    public ZookeeperClient(String zookeeperServer, String zookeeperLockPath) {
        this.zookeeperServer = zookeeperServer;
        this.zookeeperLockPath = zookeeperLockPath;
    }

    public String getZookeeperServer() {
        return zookeeperServer;
    }

    public void setZookeeperServer(String zookeeperServer) {
        this.zookeeperServer = zookeeperServer;
    }

    public String getZookeeperLockPath() {
        return zookeeperLockPath;
    }

    public void setZookeeperLockPath(String zookeeperLockPath) {
        this.zookeeperLockPath = zookeeperLockPath;
    }

    public CuratorFramework getClient() {
        return client;
    }

    public <T> T lock(AbstractZookeeperLock<T> mutex) {
        String path = this.getZookeeperLockPath() + mutex.getLockPath();
        InterProcessMutex lock = new InterProcessMutex(this.getClient(), path);
        boolean success = false;
        try {
            try {
                success = lock.acquire(mutex.getTimeout(), mutex.getTimeUnit());
            } catch (Exception e) {
                throw new RuntimeException("obtain lock error " + e.getMessage() + ", path " + path);
            }
            if (success) {
                return (T) mutex.execute();
            } else {
                return null;
            }
        } finally {
            try {
                if (success) {
                    lock.release();
                }
            } catch (Exception e) {
                logger.error("release lock error {}, path {}", e.getMessage(), path);
            }
        }
    }

    public void init() {
        this.client = CuratorFrameworkFactory.builder()
                .connectString(this.getZookeeperServer())
                .retryPolicy(new ExponentialBackoffRetry(SLEEP_TIME_MS, MAX_RETRIES))
                .build();
        this.client.start();
    }

    public void destroy() {
        try {
            if (getClient() != null) {
                getClient().close();
            }
        } catch (Exception e) {
            logger.error("stop zookeeper client error {}", e.getMessage());
        }
    }
}

