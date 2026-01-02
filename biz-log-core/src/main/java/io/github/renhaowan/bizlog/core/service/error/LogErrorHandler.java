package io.github.renhaowan.bizlog.core.service.error;

import io.github.renhaowan.bizlog.core.log.BizLogRecord;

/**
 * 异常处理 & 降级
 */
public interface LogErrorHandler {
    void onError(BizLogRecord records, Throwable ex);
}
