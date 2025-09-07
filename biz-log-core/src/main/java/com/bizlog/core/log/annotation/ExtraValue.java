package com.bizlog.core.log.annotation;

import java.lang.annotation.*;

/**
 * 拓展参数
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExtraValue {
    String k();      // map键
    String v();      // map值：支持模版语法
}
