package io.github.renhaowan.bizlog.core.service.executor;

import io.github.renhaowan.bizlog.core.log.BizLogProperties;
import io.github.renhaowan.bizlog.core.log.LogConstant;
import jakarta.annotation.PreDestroy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author wan
 * 默认的日志线程
 */
public class DefaultLogExecutorProvider implements LogExecutorProvider {

    private final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    public DefaultLogExecutorProvider(BizLogProperties prop) {
        executor.setCorePoolSize(prop.getAsync().getCorePoolSize());
        executor.setMaxPoolSize(prop.getAsync().getMaxPoolSize());
        executor.setQueueCapacity(prop.getAsync().getQueueCapacity());
        executor.setThreadNamePrefix(LogConstant.BIZ_LOG + "-");
        // 优雅停机
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(prop.getAsync().getAwaitTermination());
        // 线程池满时，调用方运行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize(); // 触发 afterPropertiesSet
    }

    @Override
    public Executor getExecutor() {
        return executor;
    }

    /* 由 Spring 容器在销毁时调用 */
    @PreDestroy
    public void destroy() {
        executor.shutdown();
    }
}
