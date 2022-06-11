package cn.edcheung.springskills.middleware.zkapp;

import cn.edcheung.springskills.middleware.zkapp.lock.AbstractZookeeperLock;

/**
 * Description TestLock
 *
 * @author Edward Cheung
 * @date 2022/3/18
 * @since JDK 1.8
 */
public abstract class TestLock<String> extends AbstractZookeeperLock<String> {
    private static final java.lang.String LOCK_PATH = "test_";

    private String lockId;

    public TestLock(String lockId) {
        this.lockId = lockId;
    }

    public String getLockId() {
        return lockId;
    }

    @Override
    public java.lang.String getLockPath() {
        return LOCK_PATH + this.lockId;
    }
}

