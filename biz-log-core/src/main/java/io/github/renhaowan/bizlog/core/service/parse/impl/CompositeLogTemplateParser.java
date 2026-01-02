package io.github.renhaowan.bizlog.core.service.parse.impl;

import io.github.renhaowan.bizlog.core.log.BizLogProperties;
import io.github.renhaowan.bizlog.core.service.error.BizLogException;
import io.github.renhaowan.bizlog.core.service.parse.LogTemplateParser;
import io.github.renhaowan.bizlog.core.service.parse.ParseContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 把系统中所有 LogTemplateParser 实现类按 @Order 排序，组成一条责任链。
 * 对外暴露的唯一入口；AOP 切面只依赖它，不直接依赖任何具体语法解析器。
 * 实现“优先级 + 降级”：
 */
@Slf4j
public class CompositeLogTemplateParser implements LogTemplateParser {
    private final List<LogTemplateParser> chain;
    private final BizLogProperties prop;

    public CompositeLogTemplateParser(List<LogTemplateParser> parsers, BizLogProperties prop) {
        this.prop = prop;
        this.chain = parsers;
    }

    @Override
    public String parse(String template, ParseContext ctx) {
        for (LogTemplateParser p : chain) {
            if (p.support(template)) {
                try {
                    return p.parse(template, ctx);
                } catch (Exception e) {
                    if (prop.getParser().isFallbackToPlain()) {
                        //解析失败回退到原文本：降级处理
                        log.warn(p.getClass().getSimpleName() + " parse error, fallback to plain text", e);
                        return template;
                    } else {
                        String name = this.getClass().getName();
                        throw new BizLogException("LogTemplateParser error: " + name, e);
                    }
                }
            }
        }
        // 兜底
        return template;
    }
}
