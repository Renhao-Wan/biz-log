package com.bizlog.core.service.storage.impl;

import com.bizlog.core.log.BizLogRecord;
import com.bizlog.core.log.LogConstant;
import com.bizlog.core.service.storage.AbstractLogStorage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 控制台日志存储
 */
@AllArgsConstructor
@Slf4j(topic = LogConstant.BIZ_LOG)
public class ConsoleLogStorage extends AbstractLogStorage {

    private ConsoleLogConfig logConfig;

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

    @Override
    protected boolean shouldStoreWhenException(Throwable ex) {
        return true;
    }

    @Override
    protected void handleException(Throwable ex) {
        log.error("【操作日志】发生异常: {}", ex.getMessage());
    }

    @Builder
    @Getter
    public static class ConsoleLogConfig {

        private String logLevel;

        public static final String DEBUG = "DEBUG";
        public static final String INFO = "INFO";
        public static final String WARN = "WARN";
        public static final String ERROR = "ERROR";
        public static final String TRACE = "TRACE";

    }
}
