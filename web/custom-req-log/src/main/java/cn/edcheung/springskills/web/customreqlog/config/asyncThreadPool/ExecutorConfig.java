package cn.edcheung.springskills.web.customreqlog.config.asyncThreadPool;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Description 异步线程池配置
 *
 * @author Edward Cheung
 * @date 2020/3/26
 * @since JDK 1.8
 */
@EnableAsync
@Configuration
public class ExecutorConfig implements AsyncConfigurer {

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        // 配置异常处理机制
        return new CustomAsyncUncaughtExceptionHandler();
    }

    /*@Bean
    public Executor singleThreadExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setKeepAliveSeconds(0);
        executor.setQueueCapacity(256);
        executor.setThreadNamePrefix("single-thread-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //executor.initialize();
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(3);
        return executor;
    }*/

    @Bean
    public Executor multiThreadExecutor() {
        // 线程池按以下行为执行任务：
        //
        // 1.当线程数小于核心线程数时，创建线程。
        // 2.当线程数大于等于核心线程数，且任务队列未满时，将任务放入任务队列。
        // 3.当线程数大于等于核心线程数，且任务队列已满
        //   1）若线程数小于最大线程数，创建线程
        //   2）若线程数等于最大线程数，抛出异常，拒绝任务

        // 线程池拒绝策略：
        //
        // 1.AbortPolicy 默认策略，直接抛出异常阻止系统正常运行
        // 2.CallerRunsPolicy “调用者运行”一种调节机制，该策略既不会抛弃任务，也不会抛出异常，而是将任务回馈至调用者所在的线程来执行
        // 3.DiscardOldestPolicy 抛弃队列中等待最久的任务，然后把当前任务加入队列中尝试再次提交当前任务
        // 4.DiscardPolicy 直接丢弃任务，不给予任何处理也不跑出异常，如果允许任务丢失，这是最好的一种方案

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        int core = Runtime.getRuntime().availableProcessors();//此方法返回可用处理器的虚拟机的最大数量,不小于1
        executor.setCorePoolSize(core);//设置核心线程数
        executor.setMaxPoolSize(core * 2 + 1);//设置最大线程数
        executor.setKeepAliveSeconds(3);//除核心线程外的线程存活时间
        //executor.setAllowCoreThreadTimeOut(true);//空闲时间达到keepAliveTime时，允许关闭核心线程
        executor.setQueueCapacity(1024);//如果传入值大于0，底层队列使用的是LinkedBlockingQueue,否则默认使用SynchronousQueue
        executor.setThreadNamePrefix("multi-thread-");//线程名称前缀
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());//设置拒绝策略
        //executor.initialize();//初始化
        executor.setWaitForTasksToCompleteOnShutdown(true);//设置线程池关闭候等待所有任务都完成后再继续销毁其依赖的Bean
        executor.setAwaitTerminationSeconds(3);//设置线程池中任务的等待时间，如果超过这个时候还没有销毁就强制销毁，以确保应⽤最后能够被关闭，⽽不是阻塞住。
        return executor;
    }
}
