package com.bizlog.core.service.error;

import com.bizlog.core.log.BizLogRecord;

/**
 * 异常处理 & 降级
 */
public interface LogErrorHandler {
    void onError(BizLogRecord records, Throwable ex);
}
