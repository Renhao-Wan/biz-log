package io.github.renhaowan.bizlog.core.service.storage;

import io.github.renhaowan.bizlog.core.log.BizLogRecord;
import org.springframework.beans.factory.BeanNameAware;

/**
 * @author wan
 * 日志存储接口
 */
public interface LogStorage extends BeanNameAware {

    /**
     * 存储日志
     *
     * @param records 日志记录
     */
    void store(BizLogRecord records);
}
