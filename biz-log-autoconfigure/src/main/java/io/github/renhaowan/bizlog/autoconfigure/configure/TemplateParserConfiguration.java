package io.github.renhaowan.bizlog.autoconfigure.configure;

import io.github.renhaowan.bizlog.core.log.BizLogProperties;
import io.github.renhaowan.bizlog.core.service.parse.LogTemplateParser;
import io.github.renhaowan.bizlog.core.service.parse.impl.CompositeLogTemplateParser;
import io.github.renhaowan.bizlog.core.service.parse.impl.PlainTextLogTemplateParser;
import io.github.renhaowan.bizlog.core.service.parse.impl.SpelLogTemplateParser;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author wan
 * 模板解析器配置类
 */
@Configuration
@RequiredArgsConstructor
public class TemplateParserConfiguration {

    private final BizLogProperties prop;

    /**
     * Spel模板解析器
     *
     * @param applicationContext Spring上下文
     * @return Spel模板解析器
     */
    @Bean
    @ConditionalOnProperty(prefix = "biz.log.parser", name = "enable-spel", havingValue = "true"
            , matchIfMissing = true)
    public SpelLogTemplateParser spelLogTemplateParser(ApplicationContext applicationContext, SpelLogTemplateParser.SpelExpansionContext spelExpansionContext) {
        return new SpelLogTemplateParser(applicationContext, prop, spelExpansionContext);
    }

    /**
     * Spel表达式上下文
     *
     * @return Spel表达式上下文
     */
    @Bean
    @ConditionalOnMissingBean(SpelLogTemplateParser.SpelExpansionContext.class)
    public SpelLogTemplateParser.SpelExpansionContext spelExpansionContext() {
        return ec -> {
            // do nothing
        };
    }

    /**
     * 普通文本模板解析器
     *
     * @return 普通文本模板解析器
     */
    @Bean
    public PlainTextLogTemplateParser plainTextLogTemplateParser() {
        return new PlainTextLogTemplateParser();
    }

    /**
     * 组合模板解析器
     *
     * @param parsers 模板解析器列表
     * @return 组合模板解析器
     */
    @Bean
    public CompositeLogTemplateParser compositeLogTemplateParser(List<LogTemplateParser> parsers) {
        return new CompositeLogTemplateParser(parsers, prop);
    }
}
