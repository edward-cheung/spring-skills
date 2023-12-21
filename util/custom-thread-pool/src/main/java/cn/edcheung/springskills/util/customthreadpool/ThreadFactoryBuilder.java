package cn.edcheung.springskills.util.customthreadpool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Java异常处理机制，当一个异常被抛出时，JVM会在当前的方法里寻找一个匹配的处理，如果没有找到，这个方法会强制结束并弹出当前栈帧，并且异常会重新抛给上层调用的方法（在调用方法帧）。
 * 如果在所有帧弹出前仍然没有找到合适的异常处理，这个线程将终止。如果这个异常在最后一个非守护线程里抛出，将会导致JVM自己终止，比如这个线程是个main线程。
 */
public class ThreadFactoryBuilder {

    /**
     * 用于线程创建的线程工厂类
     */
    private ThreadFactory backingThreadFactory;

    /**
     * 线程名的前缀
     */
    private String namePrefix;

    /**
     * 是否守护线程，默认false
     */
    private Boolean daemon;

    /**
     * 线程优先级
     */
    private Integer priority;

    /**
     * 未捕获异常处理器
     */
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    /**
     * 构建.
     *
     * @return
     */
    public static ThreadFactoryBuilder builder() {
        return new ThreadFactoryBuilder();
    }

    /**
     * 构建.
     *
     * @param builder {@link ThreadFactoryBuilder}
     * @return {@link ThreadFactory}
     */
    private static ThreadFactory build(ThreadFactoryBuilder builder) {
        final ThreadFactory backingThreadFactory = (null != builder.backingThreadFactory)
                ? builder.backingThreadFactory
                : Executors.defaultThreadFactory();
        final String namePrefix = builder.namePrefix;
        final Boolean daemon = builder.daemon;
        final Integer priority = builder.priority;
        final Thread.UncaughtExceptionHandler handler = builder.uncaughtExceptionHandler;
        final AtomicLong count = (null == namePrefix) ? null : new AtomicLong();
        return r -> {
            final Thread thread = backingThreadFactory.newThread(r);
            if (null != namePrefix) {
                thread.setName(namePrefix + "-" + count.getAndIncrement());
            }
            if (null != daemon) {
                thread.setDaemon(daemon);
            }
            if (null != priority) {
                thread.setPriority(priority);
            }
            if (null != handler) {
                thread.setUncaughtExceptionHandler(handler);
            }
            return thread;
        };
    }

    /**
     * 设置用于创建线程的线程工厂.
     *
     * @param backingThreadFactory 用于创建线程的线程工厂
     * @return this
     */
    public ThreadFactoryBuilder threadFactory(ThreadFactory backingThreadFactory) {
        this.backingThreadFactory = backingThreadFactory;
        return this;
    }

    /**
     * 设置线程名前缀, 例如设置前缀为 mb-thread- 则线程名为 mb-thread-1 之类.
     *
     * @param namePrefix 线程名前缀
     * @return this
     */
    public ThreadFactoryBuilder prefix(String namePrefix) {
        this.namePrefix = namePrefix;
        return this;
    }

    /**
     * 设置是否守护线程.
     *
     * @param daemon 是否守护线程
     * @return this
     */
    public ThreadFactoryBuilder daemon(boolean daemon) {
        this.daemon = daemon;
        return this;
    }

    /**
     * 设置线程优先级.
     *
     * @param priority 优先级
     * @return this
     * @see Thread#MIN_PRIORITY
     * @see Thread#NORM_PRIORITY
     * @see Thread#MAX_PRIORITY
     */
    public ThreadFactoryBuilder priority(int priority) {
        if (priority < Thread.MIN_PRIORITY) {
            throw new IllegalArgumentException(MessageFormat.format("Thread priority ({}) must be >= {}", priority, Thread.MIN_PRIORITY));
        }
        if (priority > Thread.MAX_PRIORITY) {
            throw new IllegalArgumentException(MessageFormat.format("Thread priority ({}) must be <= {}", priority, Thread.MAX_PRIORITY));
        }
        this.priority = priority;
        return this;
    }

    /**
     * 设置未捕获异常的处理方式.
     *
     * @param uncaughtExceptionHandler {@link Thread.UncaughtExceptionHandler}
     */
    public void uncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }

    /**
     * 构建 {@link ThreadFactory}
     *
     * @return {@link ThreadFactory}
     */
    public ThreadFactory build() {
        return build(this);
    }

    public static class CustomUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        private static final Logger logger = LoggerFactory.getLogger(CustomUncaughtExceptionHandler.class);

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            logger.error(t.toString() + "执行任务异常", e);
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else if (e instanceof Error) {
                throw (Error) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public static class CustomRejectedExecutionHandler implements RejectedExecutionHandler {

        private static final Logger logger = LoggerFactory.getLogger(CustomRejectedExecutionHandler.class);

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            logger.error(executor.toString() + "拒绝任务：" + r.getClass());
        }
    }
}
