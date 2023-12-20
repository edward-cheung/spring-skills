package cn.edcheung.springskills.io.nettyapp.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Description ParseThread
 *
 * @author Edward Cheung
 * @date 2023/12/20
 * @since JDK 1.8
 */
@Component
public class ParseThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(ParseThread.class);

    private final ArrayBlockingQueue<String> queue;

    public ParseThread() {
        this.queue = new ArrayBlockingQueue<>(100);
        this.start();
    }

    public void addPacket(String msg) {
        if (queue.offer(msg)) {
            if (logger.isDebugEnabled()) {
                logger.debug("msg: {}, queue size: {}", msg, queue.size());
            }
        } else {
            logger.error("queue full");
        }
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                String content = queue.take();
                // 数据解析
                // 数据处理
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                logger.error("数据解析异常", e);
            }
        }
    }

}
