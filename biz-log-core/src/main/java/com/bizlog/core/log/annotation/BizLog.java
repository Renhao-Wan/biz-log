package com.bizlog.core.log.annotation;

import java.lang.annotation.*;

/**
 * 业务日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BizLog {

    String value();                      // 内容，支持模板语法
    String actionCode() default "";      // 动作编码
    boolean async() default true;
    String storageBeanName() default "";   // 存储器名称
    ExtraValue[] extras() default {};       // 扩展字段
}
