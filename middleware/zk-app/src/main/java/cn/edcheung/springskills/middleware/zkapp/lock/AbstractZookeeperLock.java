package cn.edcheung.springskills.middleware.zkapp.lock;

import java.util.concurrent.TimeUnit;

/**
 * Description AbstractZookeeperLock
 *
 * @author Edward Cheung
 * @date 2022/3/18
 * @since JDK 1.8
 */
public abstract class AbstractZookeeperLock<T> {

    private static final int TIME_OUT = 5;

    public abstract String getLockPath();

    public abstract T execute();

    public int getTimeout() {
        return TIME_OUT;
    }

    public TimeUnit getTimeUnit() {
        return TimeUnit.SECONDS;
    }
}
