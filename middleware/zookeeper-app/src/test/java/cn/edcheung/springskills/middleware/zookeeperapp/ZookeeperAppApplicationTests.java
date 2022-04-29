package cn.edcheung.springskills.middleware.zookeeperapp;

import cn.edcheung.springskills.middleware.zookeeperapp.lock.ZookeeperClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

//@RunWith(SpringRunner.class)
@SpringBootTest
class ZookeeperAppApplicationTests {

    @Autowired
    private ZookeeperClient zookeeperClient;

    @Test
    void contextLoads() {
    }

    @Test
    public void test1() {
        String lockId = "123";
        String result = zookeeperClient.lock(new TestLock<String>(lockId) {
            @Override
            public String execute() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return this.getLockId();
            }
        });

        if (result == null) {
            System.out.println("执行失败");
        } else {
            System.out.println("执行成功");
        }
    }
}
