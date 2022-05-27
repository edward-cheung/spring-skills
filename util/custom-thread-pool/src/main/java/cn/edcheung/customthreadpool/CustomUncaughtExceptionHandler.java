package cn.edcheung.customthreadpool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description CustomUncaughtExceptionHandler
 *
 * @author Edward Cheung
 * @date 2020/11/17
 * @since JDK 1.8
 */
public class CustomUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomUncaughtExceptionHandler.class);

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error(t.toString(), e);
    }
}
