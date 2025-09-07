package com.bizlog.core.service.parse.impl;

import com.bizlog.core.service.parse.LogTemplateParser;
import com.bizlog.core.service.parse.ParseContext;
import org.springframework.core.annotation.Order;

/**
 * 纯文本解析器（兜底）
 */
@Order
public class PlainTextLogTemplateParser implements LogTemplateParser {
    @Override
    public String parse(String template, ParseContext ctx) {
        return template; // 原样返回
    }
}
