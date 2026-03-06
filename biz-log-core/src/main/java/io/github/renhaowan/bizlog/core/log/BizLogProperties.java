package io.github.renhaowan.bizlog.core.log;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * BizLog 配置属性类
 * <p>
 * 用于配置业务日志框架的各项参数，包括全局开关、存储器配置、异步线程池参数和模板解析器参数。
 * 配置前缀为：biz.log
 * </p>
 * 
 * @author wan
 * @since 0.0.1
 */
@Data
@ConfigurationProperties(prefix = "biz.log")
public class BizLogProperties {

    /**
     * 全局总开关，控制是否启用 BizLog 功能
     * <p>默认值：true</p>
     */
    private boolean enabled = true;

    /**
     * 默认存储器 Bean 名称数组
     * <p>支持配置多个存储器 Bean 名称，按顺序执行</p>
     * <p>默认值：["consoleLogStorage"]</p>
     */
    private String[] storageBeanName = new String[]{LogConstant.DEFAULT_STORAGE_BEAN_NAME};

    /**
     * 异步线程池参数配置
     */
    private final Async async = new Async();

    /**
     * 模板解析器参数配置
     */
    private final Parser parser = new Parser();

    /**
     * 异步线程池参数配置类
     */
    @Data
    public static class Async {
        /**
         * 核心线程数
         * <p>默认值：4</p>
         */
        private int corePoolSize = 4;
        
        /**
         * 最大线程数
         * <p>默认值：8</p>
         */
        private int maxPoolSize  = 8;
        
        /**
         * 队列容量
         * <p>默认值：200</p>
         */
        private int queueCapacity = 200;
        
        /**
         * 线程名前缀
         * <p>默认值："BizLog-"</p>
         */
        private String threadNamePrefix = "BizLog-";
        
        /**
         * 优雅停机等待时间（单位：秒）
         * <p>默认值：30</p>
         */
        private int awaitTermination = 30;
    }

    /**
     * 模板解析器参数配置类
     */
    @Data
    public static class Parser {
        /**
         * 解析失败是否回退到原文本
         * <p>当模板解析失败时，是否使用原始文本作为日志内容</p>
         * <p>默认值：true</p>
         */
        private boolean fallbackToPlain = true;
        
        /**
         * SpEL 解析器参数配置
         */
        private final Spel spel = new Spel();

        /**
         * SpEL 解析器参数配置类
         */
        @Data
        public static class Spel {
            /**
             * 是否启用 SpEL 解析器
             * <p>控制是否启用 Spring Expression Language 解析功能</p>
             * <p>默认值：true</p>
             */
            private boolean enabled = true;
        }
    }

}
