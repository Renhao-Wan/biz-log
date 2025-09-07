package com.bizlog.autoconfigure;

import com.bizlog.autoconfigure.configure.LogErrorHandlerConfiguration;
import com.bizlog.autoconfigure.configure.LogExecutorConfiguration;
import com.bizlog.autoconfigure.configure.LogStorageConfiguration;
import com.bizlog.autoconfigure.configure.TemplateParserConfiguration;
import com.bizlog.core.log.BizLogProperties;
import com.bizlog.core.service.BizLogAspect;
import com.bizlog.core.service.BizLogManager;
import com.bizlog.core.service.error.LogErrorHandler;
import com.bizlog.core.service.executor.LogExecutorProvider;
import com.bizlog.core.service.parse.impl.CompositeLogTemplateParser;
import com.bizlog.core.service.storage.LogStorageManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.Map;

/**
 * 自动配置类
 */
@AutoConfiguration
@EnableConfigurationProperties(BizLogProperties.class)
@ConditionalOnProperty(prefix = "biz.log", name = "enabled", havingValue = "true"
        , matchIfMissing = true)
@Import({TemplateParserConfiguration.class, LogStorageConfiguration.class,
        LogExecutorConfiguration.class, LogErrorHandlerConfiguration.class})
public class BizLogAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(BizLogAspect.class)
    public BizLogAspect bizLogAspect(BizLogManager manager, BizLogAspect.ExtendParseContextExtraValue extraValue) {
        return new BizLogAspect(manager, extraValue);
    }

    @Bean
    @ConditionalOnMissingBean(BizLogAspect.ExtendParseContextExtraValue.class)
    public BizLogAspect.ExtendParseContextExtraValue extendParseContextExtraValue() {
        return Map::of;
    }

    @Bean
    @ConditionalOnMissingBean(BizLogManager.class)
    public BizLogManager bizLogManager(LogStorageManager storageManager,
                                       CompositeLogTemplateParser parser,
                                       LogExecutorProvider executor,
                                       LogErrorHandler errorHandler) {
        return new BizLogManager(storageManager, parser, executor, errorHandler);
    }

}
