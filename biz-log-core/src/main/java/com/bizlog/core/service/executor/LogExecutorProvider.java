package com.bizlog.core.service.executor;

import java.util.concurrent.Executor;

/**
 * 日志执行器提供者: 线程池策略
 */
public interface LogExecutorProvider {
    Executor getExecutor();
}
