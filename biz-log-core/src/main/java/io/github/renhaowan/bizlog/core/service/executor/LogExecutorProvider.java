package io.github.renhaowan.bizlog.core.service.executor;

import java.util.concurrent.Executor;

/**
 * @author wan
 * 日志执行器提供者: 线程池策略
 */
public interface LogExecutorProvider {

    /**
     * 获取日志执行器(线程池)
     * @return 日志执行器
     */
    Executor getExecutor();
}
