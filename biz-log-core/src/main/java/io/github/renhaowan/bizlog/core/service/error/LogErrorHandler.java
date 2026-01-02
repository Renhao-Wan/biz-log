package io.github.renhaowan.bizlog.core.service.error;

import io.github.renhaowan.bizlog.core.log.BizLogRecord;

/**
 * @author wan
 * 异常处理 和 降级
 */
public interface LogErrorHandler {

    /**
     * 错误处理
     *
     * @param records 日志记录
     * @param ex      错误
     */
    void onError(BizLogRecord records, Throwable ex);
}
