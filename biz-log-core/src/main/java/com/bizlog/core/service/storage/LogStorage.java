package com.bizlog.core.service.storage;

import com.bizlog.core.log.BizLogRecord;
import org.springframework.beans.factory.BeanNameAware;

/**
 * 日志存储接口
 */
public interface LogStorage extends BeanNameAware {
    void store(BizLogRecord records);
}
