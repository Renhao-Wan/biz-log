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
 * @author wan
 * 默认存储器配置
 */
@Configuration
public class LogStorageConfiguration {

    /**
     * 默认控制台日志存储器
     *
     * @param logConfig 控制台日志配置
     * @return 控制台日志存储器
     */
    @Bean(LogConstant.DEFAULT_STORAGE_BEAN_NAME)
    @ConditionalOnMissingBean(ConsoleLogStorage.class)
    public AbstractLogStorage consoleLogStorage(ConsoleLogStorage.ConsoleLogConfig logConfig) {
        return new ConsoleLogStorage(logConfig);
    }

    /**
     * 默认控制台日志配置
     *
     * @return 控制台日志配置
     */
    @Bean
    @ConditionalOnMissingBean(ConsoleLogStorage.ConsoleLogConfig.class)
    public ConsoleLogStorage.ConsoleLogConfig logLevel() {
        return ConsoleLogStorage.ConsoleLogConfig.builder()
                .logLevel(ConsoleLogStorage.ConsoleLogConfig.INFO)
                .build();
    }

    /**
     * 默认日志存储器管理器
     *
     * @param storageList 日志存储器列表
     * @param prop        日志配置
     * @return 日志存储器管理器
     */
    @Bean
    @ConditionalOnMissingBean(LogStorageManager.class)
    public LogStorageManager logStorageManager(List<AbstractLogStorage> storageList, BizLogProperties prop) {
        return new LogStorageManager(storageList, prop);
    }
}
