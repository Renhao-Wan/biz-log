package io.github.renhaowan.bizlog.autoconfigure;

import io.github.renhaowan.bizlog.autoconfigure.configure.LogErrorHandlerConfiguration;
import io.github.renhaowan.bizlog.autoconfigure.configure.LogExecutorConfiguration;
import io.github.renhaowan.bizlog.autoconfigure.configure.LogStorageConfiguration;
import io.github.renhaowan.bizlog.autoconfigure.configure.TemplateParserConfiguration;
import io.github.renhaowan.bizlog.core.log.BizLogProperties;
import io.github.renhaowan.bizlog.core.service.BizLogAspect;
import io.github.renhaowan.bizlog.core.service.BizLogManager;
import io.github.renhaowan.bizlog.core.service.error.LogErrorHandler;
import io.github.renhaowan.bizlog.core.service.executor.LogExecutorProvider;
import io.github.renhaowan.bizlog.core.service.parse.impl.CompositeLogTemplateParser;
import io.github.renhaowan.bizlog.core.service.storage.LogStorageManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.Map;

/**
 * @author wan
 * 自动配置类
 */
@AutoConfiguration
@EnableConfigurationProperties(BizLogProperties.class)
@ConditionalOnProperty(prefix = "biz.log", name = "enabled", havingValue = "true"
        , matchIfMissing = true)
@Import({TemplateParserConfiguration.class, LogStorageConfiguration.class,
        LogExecutorConfiguration.class, LogErrorHandlerConfiguration.class})
public class BizLogAutoConfiguration {

    /**
     * 日志切面
     *
     * @param manager 日志管理器
     * @param extraValue 额外的参数
     * @return 日志切面
     */
    @Bean
    @ConditionalOnMissingBean(BizLogAspect.class)
    public BizLogAspect bizLogAspect(BizLogManager manager, BizLogAspect.ExtendParseContextExtraValue extraValue) {
        return new BizLogAspect(manager, extraValue);
    }

    /**
     * 额外的参数
     *
     * @return 额外的参数
     */
    @Bean
    @ConditionalOnMissingBean(BizLogAspect.ExtendParseContextExtraValue.class)
    public BizLogAspect.ExtendParseContextExtraValue extendParseContextExtraValue() {
        return Map::of;
    }

    /**
     * 日志管理器
     *
     * @param storageManager 日志存储器管理器
     * @param parser 日志模板解析器
     * @param executor 日志执行器
     * @param errorHandler 日志异常处理器
     * @return 日志管理器
     */
    @Bean
    @ConditionalOnMissingBean(BizLogManager.class)
    public BizLogManager bizLogManager(LogStorageManager storageManager,
                                       CompositeLogTemplateParser parser,
                                       LogExecutorProvider executor,
                                       LogErrorHandler errorHandler) {
        return new BizLogManager(storageManager, parser, executor, errorHandler);
    }

}
