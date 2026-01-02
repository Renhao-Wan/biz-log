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
 * 对外暴露：注解 + 管理器两种使用方式
 */
@Slf4j(topic = LogConstant.BIZ_LOG)
public class BizLogManager implements InitializingBean {

    private final LogStorageManager storageManager;
    private final CompositeLogTemplateParser parser;
    private final Executor executor;
    private final LogErrorHandler errorHandler;

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
    public void record(BizLogRecord record, String... storageBeanName) {
        storageManager.store(record, storageBeanName);
    }

    /* ========== 自定义存储器异步记录 ========== */
    public CompletableFuture<Void> recordAsync(BizLogRecord record, String... storageBeanName) {
            return CompletableFuture.runAsync(() -> record(record, storageBeanName), executor);
    }

    /* ========== 快捷方法 —— 手动调用 ========== */
    public void record(String actionCode, String content, boolean async, Map<String, Object> extra, String... storageBeanName) {
        BizLogRecord r = BizLogRecord.builder()
                .action(BizActions.of(actionCode))
                .content(content)
                .extra(extra)
                .time(LocalDateTime.now())
                .build();
        recordChoose(async, r, storageBeanName);
    }

    public void record(String actionCode, String content,
                       boolean async, String... storageBeanName) {
        record(actionCode, content, async, null, storageBeanName);
    }

    /* ========== 解析模板并记录（供切面调用） ========== */
    public void record(String template,
                       ParseContext ctx,
                       boolean async, String[] storageBeanName) {
        String content = parser.parse(template, ctx);             // 解析模板
        Map<String, Object> extra = ctx.getExtra()
                .entrySet()
                .stream()
                .filter(e -> !"actionCode".equals(e.getKey()))
                .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        parserExtraValues(extra, ctx);        // 解析 extra （解析ExtraValue注解中的v）
        BizLogRecord record = BizLogRecord.builder()
                .action(BizActions.of((String) ctx.getExtra().get("actionCode")))
                .content(content)
                .throwable(ctx.getThrown())
                .extra(extra)              // 过滤掉 actionCode 和 bizId
                .time(LocalDateTime.now())
                .build();
        recordChoose(async, record, storageBeanName);
    }

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

    // 解析 extra 中的 ExtraValue 注解
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