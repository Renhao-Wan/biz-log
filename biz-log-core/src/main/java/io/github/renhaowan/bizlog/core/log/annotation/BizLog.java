package io.github.renhaowan.bizlog.core.log.annotation;

import java.lang.annotation.*;

/**
 * @author wan
 * 业务日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BizLog {

    /**
     * 日志内容，支持模板语法
     */
    String value();

    /**
     * 动作编码
     */
    String actionCode() default "";

    /**
     * 是否异步记录，默认异步
     */
    boolean async() default true;

    /**
     * 自定义存储器名称，默认使用默认存储器
     */
    String[] storageBeanName() default {};

    /**
     * 扩展参数
     */
    ExtraValue[] extras() default {};
}
