package com.bizlog.autoconfigure.configure;

import com.bizlog.core.log.LogConstant;
import com.bizlog.core.service.error.DefaultLogErrorHandler;
import com.bizlog.core.service.error.LogErrorHandler;
import com.bizlog.core.service.storage.AbstractLogStorage;
import com.bizlog.core.service.storage.impl.ConsoleLogStorage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 默认异常处理器
 */
@Configuration
public class LogErrorHandlerConfiguration {

    @Bean
    @ConditionalOnMissingBean(LogErrorHandler.class)
    public LogErrorHandler logErrorHandler(@Qualifier(LogConstant.DEFAULT_STORAGE_BEAN_NAME)
                                               AbstractLogStorage consoleLogStorage) {
        return new DefaultLogErrorHandler((ConsoleLogStorage) consoleLogStorage);
    }
}
