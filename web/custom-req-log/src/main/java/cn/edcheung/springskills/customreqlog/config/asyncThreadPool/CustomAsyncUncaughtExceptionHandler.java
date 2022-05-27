package cn.edcheung.springskills.customreqlog.config.asyncThreadPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

/**
 * Description CustomAsyncUncaughtExceptionHandler
 *
 * @author Edward Cheung
 * @date 2020/12/8
 * @since JDK 1.8
 */
public class CustomAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomAsyncUncaughtExceptionHandler.class);

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... params) {
        log.error("异步多线程执行异常。方法：[{}]，异常信息[{}] : {}", method, throwable.getMessage(), throwable);
    }
}
