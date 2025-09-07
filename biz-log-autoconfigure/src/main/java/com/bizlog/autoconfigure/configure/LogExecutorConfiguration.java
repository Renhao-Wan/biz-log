package com.bizlog.autoconfigure.configure;

import com.bizlog.core.log.BizLogProperties;
import com.bizlog.core.service.executor.DefaultLogExecutorProvider;
import com.bizlog.core.service.executor.LogExecutorProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 默认线程池
 */
@Configuration
public class LogExecutorConfiguration {

    @Bean
    @ConditionalOnMissingBean(LogExecutorProvider.class)
    public LogExecutorProvider logExecutorProvider(BizLogProperties prop) {
        return new DefaultLogExecutorProvider(prop);
    }
}
