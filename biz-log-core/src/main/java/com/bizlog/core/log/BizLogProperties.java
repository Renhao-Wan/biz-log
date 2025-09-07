package com.bizlog.core.log;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * biz-log 配置属性
 */
@Data
@ConfigurationProperties(prefix = "biz.log")
public class BizLogProperties {

    /** 全局总开关 */
    private boolean enabled = true;

    /** 默认存储器 */
    private String storageBeanName = LogConstant.DEFAULT_STORAGE_BEAN_NAME;

    /** 异步线程池参数 */
    private final Async async = new Async();

    /** 模板解析器参数 */
    private final Parser parser = new Parser();

    @Data
    public static class Async {
        /** 核心线程数 */
        private int corePoolSize = 4;
        /** 最大线程数 */
        private int maxPoolSize  = 8;
        /** 队列容量 */
        private int queueCapacity = 200;
        /** 线程名前缀 */
        private String threadNamePrefix = "BizLog-";
        /** 优雅停机等待时间 ： s*/
        private int awaitTermination = 30;
    }

    @Data
    public static class Parser {
        /** 解析失败是否回退到原文本 */
        private boolean fallbackToPlain = true;
        /** Spel解析器 */
        private final Spel spel = new Spel();

        @Data
        public static class Spel {
            /** 是否启用 SpEL 解析器 */
            private boolean enabled = true;
            /** SpEL 解析器缓存大小 */
            private int cacheSize = 100;
            /** SpEL 解析器缓存时间 ： s*/
            private int cacheTime = 120;      //expireAfterAccess: 最后访问后多久过期
        }
    }

}
