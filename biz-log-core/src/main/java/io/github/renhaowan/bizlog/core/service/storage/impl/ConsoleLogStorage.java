package io.github.renhaowan.bizlog.core.service.storage.impl;

import io.github.renhaowan.bizlog.core.log.BizLogRecord;
import io.github.renhaowan.bizlog.core.log.LogConstant;
import io.github.renhaowan.bizlog.core.service.storage.AbstractLogStorage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * @author wan
 * 控制台日志存储
 */
@AllArgsConstructor
@Slf4j(topic = LogConstant.BIZ_LOG)
public class ConsoleLogStorage extends AbstractLogStorage {

    private ConsoleLogConfig logConfig;

    /**
     * 存储日志
     *
     * @param records 日志记录
     */
    @Override
    public void store(BizLogRecord records) {
        switch (logConfig.getLogLevel().toUpperCase()){
            case ConsoleLogConfig.DEBUG:
                log.debug(combineRecords(records));
                break;
            case ConsoleLogConfig.WARN:
                log.warn(combineRecords(records));
                break;
            case ConsoleLogConfig.ERROR:
                log.error(combineRecords(records));
                break;
            case ConsoleLogConfig.TRACE:
                log.trace(combineRecords(records));
                break;
            default:
                log.info(combineRecords(records));
        }
    }

    // 组合records
    private String combineRecords(BizLogRecord records) {
        StringBuilder sb = new StringBuilder();
        sb.append("【操作日志】");
        sb.append(" | 操作类型: ").append(records.getAction() != null ? records.getAction().getDesc() : "未指定");
        sb.append(" | 操作内容: ").append(records.getContent() != null ? records.getContent() : "无");
        sb.append(" | 操作时间: ").append(records.getTime() != null ?
                records.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "无");
        // 处理额外信息
        if (records.getExtra() != null && !records.getExtra().isEmpty()) {
            sb.append(" | 扩展信息: ");
            for (Map.Entry<String, Object> entry : records.getExtra().entrySet()){
                sb.append(entry.getKey()).append(": ")
                        .append(entry.getValue()).append(" , ");
            }
            sb.delete(sb.length() - 2, sb.length());
        }

        return sb.toString();
    }

    /**
     * 是否处理异常
     *
     * @param ex 异常
     * @return true: 处理异常
     */
    @Override
    protected boolean shouldStoreWhenException(Throwable ex) {
        return true;
    }

    /**
     * 处理异常
     *
     * @param ex 异常
     */
    @Override
    protected void handleException(Throwable ex) {
        log.error("【操作日志】发生异常: {}", ex.getMessage());
    }

    /**
     * 控制台日志配置
     */
    @Builder
    @Getter
    public static class ConsoleLogConfig {

        private String logLevel;

        /**
         * 日志级别: DEBUG
         */
        public static final String DEBUG = "DEBUG";

        /**
         * 日志级别: INFO
         */
        public static final String INFO = "INFO";

        /**
         * 日志级别: WARN
         */
        public static final String WARN = "WARN";

        /**
         * 日志级别: ERROR
         */
        public static final String ERROR = "ERROR";

        /**
         * 日志级别: TRACE
         */
        public static final String TRACE = "TRACE";

    }
}
