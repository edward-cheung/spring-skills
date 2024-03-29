package cn.edcheung.springskills.web.customreqlog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * Description 计划任务配置
 *
 * @author Edward Cheung
 * @date 2020/12/8
 * @since JDK 1.8
 */
@EnableScheduling
@Configuration
public class ScheduledTaskConfig implements SchedulingConfigurer {

    @Scheduled(fixedRate = 3 * 1000L)
    public void test() {
        System.out.println("hello world");
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(Runtime.getRuntime().availableProcessors() + 1);
        // 如果ErrorHandler抛出异常，当前任务将不会继续执行
        scheduler.setErrorHandler(throwable -> System.out.println(throwable.getMessage()));
        scheduler.initialize();
        scheduledTaskRegistrar.setTaskScheduler(scheduler);
        // scheduledTaskRegistrar.setScheduler(Executors.newScheduledThreadPool(10));
    }
}
