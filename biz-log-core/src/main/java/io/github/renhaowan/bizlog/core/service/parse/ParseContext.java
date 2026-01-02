package io.github.renhaowan.bizlog.core.service.parse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author wan
 * 解析上下文
 * 把所有运行时数据打包成一个不可变对象
 * 在 AOP 切面里一次性把所有可能与模板相关的数据收集进来，交给解析器；解析器不再关心底
 */
@Getter
@Builder
@AllArgsConstructor
public final class ParseContext {
    // 被拦截的方法
    private final Method method;

    // 原始实参
    private final Map<String, Object> args;

    // 方法返回值（异常时为 null）
    private final Object retValue;

    // 抛出的异常
    private final Throwable thrown;

    // 用户自定义额外变量
    private final Map<String, Object> extra;
}