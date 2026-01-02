package io.github.renhaowan.bizlog.autoconfigure.configure;

import io.github.renhaowan.bizlog.core.log.BizLogProperties;
import io.github.renhaowan.bizlog.core.log.LogConstant;
import io.github.renhaowan.bizlog.core.service.storage.AbstractLogStorage;
import io.github.renhaowan.bizlog.core.service.storage.LogStorageManager;
import io.github.renhaowan.bizlog.core.service.storage.impl.ConsoleLogStorage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 默认存储器配置
 */
@Configuration
public class LogStorageConfiguration {
    @Bean(LogConstant.DEFAULT_STORAGE_BEAN_NAME)
    @ConditionalOnMissingBean(ConsoleLogStorage.class)
    public AbstractLogStorage consoleLogStorage(ConsoleLogStorage.ConsoleLogConfig logConfig) {
        return new ConsoleLogStorage(logConfig);
    }

    @Bean
    @ConditionalOnMissingBean(ConsoleLogStorage.ConsoleLogConfig.class)
    public ConsoleLogStorage.ConsoleLogConfig logLevel() {
        return ConsoleLogStorage.ConsoleLogConfig.builder()
                .logLevel(ConsoleLogStorage.ConsoleLogConfig.INFO)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(LogStorageManager.class)
    public LogStorageManager logStorageManager(List<AbstractLogStorage> storageList, BizLogProperties prop) {
        return new LogStorageManager(storageList, prop);
    }
}
