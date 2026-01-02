package io.github.renhaowan.bizlog.core.log.annotation;

import java.lang.annotation.*;

/**
 * @author wan
 * 拓展参数
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExtraValue {
    /**
     * key: map key
     */
    String k();

    /**
     * value: map value， 支持模板解析
     */
    String v();
}
