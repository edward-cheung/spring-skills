package cn.edcheung.springskills.middleware.zookeeperapp.use;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Description CuratorServiceImpl
 *
 * @author Edward Cheung
 * @date 2022/3/18
 * @since JDK 1.8
 */
@Service
public class CuratorServiceImpl implements CuratorService {

    @Value("${zookeeper.connect.waitTime}")
    private int waitTime;
    @Autowired
    private CuratorFramework curatorClient;

    @Override
    public String createNode(String path, String data) throws Exception {
        String nodePath = curatorClient.create().creatingParentsIfNeeded().forPath(path, data.getBytes());
        return nodePath;
    }

    @Override
    public String createTypeNode(CreateMode nodeType, String path, String data) throws Exception {
        String nodePath = curatorClient.create().creatingParentsIfNeeded().withMode(nodeType).forPath(path, data.getBytes());
        return nodePath;
    }

    @Override
    public String createTypeSeqNode(CreateMode nodeType, String path, String data) throws Exception {
        String nodePath = curatorClient.create().creatingParentsIfNeeded().withProtection().withMode(nodeType).forPath(path, data.getBytes());
        return nodePath;
    }

    @Override
    public Stat setData(String path, String data) throws Exception {
        Stat stat = curatorClient.setData().forPath(path, data.getBytes());
        return stat;
    }

    @Override
    public Stat setDataAsync(String path, String data) throws Exception {
        CuratorListener listener = new CuratorListener() {
            @Override
            public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
                //examine event for details
            }
        };
        curatorClient.getCuratorListenable().addListener(listener);
        Stat stat = curatorClient.setData().inBackground().forPath(path, data.getBytes());
        return stat;
    }

    @Override
    public void deleteNode(String path) throws Exception {
        curatorClient.delete().deletingChildrenIfNeeded().forPath(path);
    }

    @Override
    public List<String> watchedGetChildren(String path) throws Exception {
        List<String> childrenList = curatorClient.getChildren().watched().forPath(path);
        return childrenList;
    }

    @Override
    public List<String> watchedGetChildren(String path, Watcher watcher) throws Exception {
        List<String> childrenList = curatorClient.getChildren().usingWatcher(watcher).forPath(path);
        return childrenList;
    }

    @Override
    public void getLock(String path) {
        InterProcessLock lock = new InterProcessMutex(curatorClient, path);
        try {
            if (lock.acquire(waitTime, TimeUnit.MILLISECONDS)) {
                System.out.println("to do something");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                lock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getDistributedId(String path, String data) throws Exception {
        String seqNode = this.createTypeSeqNode(CreateMode.EPHEMERAL_SEQUENTIAL, "/" + path, data);
        System.out.println(seqNode);
        int index = seqNode.lastIndexOf(path);
        if (index >= 0) {
            index += path.length();
            return index <= seqNode.length() ? seqNode.substring(index) : "";
        }
        return seqNode;
    }
}

