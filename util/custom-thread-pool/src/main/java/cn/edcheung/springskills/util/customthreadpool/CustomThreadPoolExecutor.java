package cn.edcheung.springskills.util.customthreadpool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class CustomThreadPoolExecutor extends ThreadPoolExecutor {

    private static final Logger log = LoggerFactory.getLogger(CustomThreadPoolExecutor.class);

    private static final ThreadLocal<Long> TASK_CONTEXT = new ThreadLocal<>();

    private final ReentrantLock mainLock = new ReentrantLock();

    private long minimumExecuteTime = 0L;
    private long maximumExecuteTime = 0L;
    private long averageExecuteTime = 0L;

    public CustomThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public CustomThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public CustomThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public CustomThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    public void execute(Runnable task) {
        super.execute(ThreadMdcUtil.wrap(task, MDC.getCopyOfContextMap()));
    }

    @Override
    @NonNull
    public <T> Future<T> submit(Runnable task, T result) {
        return super.submit(ThreadMdcUtil.wrap(task, MDC.getCopyOfContextMap()), result);
    }

    @Override
    @NonNull
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(ThreadMdcUtil.wrap(task, MDC.getCopyOfContextMap()));
    }

    @Override
    @NonNull
    public Future<?> submit(Runnable task) {
        return super.submit(ThreadMdcUtil.wrap(task, MDC.getCopyOfContextMap()));
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        TASK_CONTEXT.set(System.currentTimeMillis());
        // Subclasses should generally invoke super.beforeExecute at the end of this method.
        super.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        // Subclasses should generally invoke super.afterExecute at the beginning of this method.
        super.afterExecute(r, t);
        if (t == null) {
            // 只统计执行成功的任务
            final long executeTime = System.currentTimeMillis() - TASK_CONTEXT.get();
            log.info("执行时间：" + executeTime);
            final ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try {
                if (minimumExecuteTime == 0L) {
                    this.minimumExecuteTime = executeTime;
                } else {
                    this.minimumExecuteTime = Math.min(minimumExecuteTime, executeTime);
                }
                this.maximumExecuteTime = Math.max(maximumExecuteTime, executeTime);
            } finally {
                mainLock.unlock();
            }
        }
        TASK_CONTEXT.remove();
    }
}
