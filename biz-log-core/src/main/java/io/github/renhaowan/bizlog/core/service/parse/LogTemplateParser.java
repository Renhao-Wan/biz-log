package io.github.renhaowan.bizlog.core.service.parse;

/**
 * @author wan
 * 日志模板解析器
 */
public interface LogTemplateParser {
    /**
     * 解析模板
     * @param template 原始模板，如 "用户#{operator}删除课程：#{course.name}"
     * @param ctx      运行时上下文
     * @return 解析后的纯文本
     */
    String parse(String template, ParseContext ctx);

    /**
     * 模板是否支持当前语法
     * 用于责任链快速跳过
     * @param template 模板
     * @return 是否支持
     */
    default boolean support(String template) {
        return true;
    }
}
