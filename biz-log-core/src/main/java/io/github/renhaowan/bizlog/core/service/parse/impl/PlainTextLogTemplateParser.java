package io.github.renhaowan.bizlog.core.service.parse.impl;

import io.github.renhaowan.bizlog.core.service.parse.LogTemplateParser;
import io.github.renhaowan.bizlog.core.service.parse.ParseContext;
import org.springframework.core.annotation.Order;

/**
 * @author wan
 * 纯文本解析器（兜底）
 */
@Order
public class PlainTextLogTemplateParser implements LogTemplateParser {
    @Override
    public String parse(String template, ParseContext ctx) {
        // 原样返回
        return template;
    }
}
