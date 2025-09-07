package com.bizlog.autoconfigure.configure;

import com.bizlog.core.log.BizLogProperties;
import com.bizlog.core.service.parse.LogTemplateParser;
import com.bizlog.core.service.parse.impl.CompositeLogTemplateParser;
import com.bizlog.core.service.parse.impl.PlainTextLogTemplateParser;
import com.bizlog.core.service.parse.impl.SpelLogTemplateParser;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 模板解析器配置类
 */
@Configuration
@RequiredArgsConstructor
public class TemplateParserConfiguration {

    private final BizLogProperties prop;

    @Bean
    @ConditionalOnProperty(prefix = "biz.log.parser", name = "enable-spel", havingValue = "true"
            , matchIfMissing = true)
    public SpelLogTemplateParser spelLogTemplateParser(ApplicationContext applicationContext, SpelLogTemplateParser.SpelExpansionContext spelExpansionContext) {
        return new SpelLogTemplateParser(applicationContext, prop, spelExpansionContext);
    }

    @Bean
    @ConditionalOnMissingBean(SpelLogTemplateParser.SpelExpansionContext.class)
    public SpelLogTemplateParser.SpelExpansionContext spelExpansionContext() {
        return ec -> {
            // do nothing
        };
    }

    @Bean
    public PlainTextLogTemplateParser plainTextLogTemplateParser() {
        return new PlainTextLogTemplateParser();
    }

    @Bean
    public CompositeLogTemplateParser compositeLogTemplateParser(List<LogTemplateParser> parsers) {
        return new CompositeLogTemplateParser(parsers, prop);
    }
}
