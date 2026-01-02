package io.github.renhaowan.bizlog.core.service;

import io.github.renhaowan.bizlog.core.log.BizLogRecord;
import io.github.renhaowan.bizlog.core.log.LogConstant;
import io.github.renhaowan.bizlog.core.log.action.BizActions;
import io.github.renhaowan.bizlog.core.service.error.LogErrorHandler;
import io.github.renhaowan.bizlog.core.service.executor.LogExecutorProvider;
import io.github.renhaowan.bizlog.core.service.parse.ParseContext;
import io.github.renhaowan.bizlog.core.service.parse.impl.CompositeLogTemplateParser;
import io.github.renhaowan.bizlog.core.service.storage.LogStorageManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author wan
 * 对外暴露：注解 + 管理器两种使用方式
 */
@Slf4j(topic = LogConstant.BIZ_LOG)
public class BizLogManager implements InitializingBean {

    private final LogStorageManager storageManager;
    private final CompositeLogTemplateParser parser;
    private final Executor executor;
    private final LogErrorHandler errorHandler;

    /**
     * @param storageManager 日志存储管理器
     * @param parser 日志模板解析器
     * @param executorProvider 日志记录执行器提供者
     * @param errorHandler 日志记录错误处理器
     */
    public BizLogManager(LogStorageManager storageManager,
                         CompositeLogTemplateParser parser,
                         LogExecutorProvider executorProvider,
                         LogErrorHandler errorHandler) {
        this.storageManager = storageManager;
        this.parser = parser;
        this.executor = executorProvider.getExecutor();
        this.errorHandler = errorHandler;
    }

    /* ========== 自定义存储器同步记录 ========== */

    /**
     * 记录日志
     * @param record 日志记录
     * @param storageBeanName 存储器名称
     */
    public void record(BizLogRecord record, String... storageBeanName) {
        storageManager.store(record, storageBeanName);
    }

    /* ========== 自定义存储器异步记录 ========== */

    /**
     * 异步记录日志
     * @param record 日志记录
     * @param storageBeanName 存储器名称
     * @return CompletableFuture
     */
    public CompletableFuture<Void> recordAsync(BizLogRecord record, String... storageBeanName) {
            return CompletableFuture.runAsync(() -> record(record, storageBeanName), executor);
    }

    /* ========== 快捷方法 —— 手动调用 ========== */

    /**
     * 手动记录日志
     * @param actionCode 动作码
     * @param content 日志内容
     * @param async 是否异步
     * @param extra 额外参数
     * @param storageBeanName 存储器名称
     */
    public void record(String actionCode, String content, boolean async, Map<String, Object> extra, String... storageBeanName) {
        BizLogRecord r = BizLogRecord.builder()
                .action(BizActions.of(actionCode))
                .content(content)
                .extra(extra)
                .time(LocalDateTime.now())
                .build();
        recordChoose(async, r, storageBeanName);
    }

    /**
     * 手动记录日志
     * @param actionCode 动作码
     * @param content 日志内容
     * @param async 是否异步
     * @param storageBeanName 存储器名称
     */
    public void record(String actionCode, String content,
                       boolean async, String... storageBeanName) {
        record(actionCode, content, async, null, storageBeanName);
    }

    /* ========== 解析模板并记录（供切面调用） ========== */

    /**
     * 解析模板并记录
     * @param template 模板
     * @param ctx 解析上下文
     * @param async 是否异步
     * @param storageBeanName 存储器名称
     */
    public void record(String template,
                       ParseContext ctx,
                       boolean async, String[] storageBeanName) {
        // 解析模板
        String content = parser.parse(template, ctx);
        Map<String, Object> extra = ctx.getExtra()
                .entrySet()
                .stream()
                .filter(e -> !"actionCode".equals(e.getKey()))
                .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        // 解析 extra （解析ExtraValue注解中的v）
        parserExtraValues(extra, ctx);
        BizLogRecord record = BizLogRecord.builder()
                .action(BizActions.of((String) ctx.getExtra().get("actionCode")))
                .content(content)
                .throwable(ctx.getThrown())
                .extra(extra)              // 过滤掉 actionCode 和 bizId
                .time(LocalDateTime.now())
                .build();
        recordChoose(async, record, storageBeanName);
    }

    /**
     * 选择记录方式
     * @param async 是否异步
     * @param record 日志记录
     * @param storageBeanName 存储器名称
     */
    private void recordChoose(boolean async, BizLogRecord record, String[] storageBeanName){
        if (async) {
            recordAsync(record, storageBeanName)
                    .whenComplete((v, e) -> {
                        if (e != null) {
                            errorHandler.onError(record, e);
                        }
                    });
        } else {
            try {
                record(record, storageBeanName);
            } catch (Exception e) {
                errorHandler.onError(record, e);
            }
        }
    }

    /* Spring 容器启动后打印线程池信息 */
    @Override
    public void afterPropertiesSet() {
        log.info("BizLogManager initialized, defaultStorage={}, executor={}, errorHandler={}",
                storageManager.getDefaultStorageBeanName(), executor.getClass().getSimpleName()
                , errorHandler.getClass().getSimpleName()
        );
    }

    /**
     * 解析 extra 中的 ExtraValue 注解
     * @param extra 待解析的 extra
     * @param ctx 解析上下文
     */
    private void parserExtraValues(Map<String, Object> extra, ParseContext ctx) {
        if (extra == null) {
            return;
        }
        for (Map.Entry<String, Object> entry : extra.entrySet()) {
            if (entry.getValue() instanceof String v) {
                entry.setValue(parser.parse(v, ctx));
            }
        }
    }
}