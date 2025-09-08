package com.bizlog.core.service.storage;

import com.bizlog.core.log.BizLogRecord;
import lombok.Getter;

/**
 * 抽象日志存储器
 * 获取日志存储器的beanName
 */
@Getter
public abstract class AbstractLogStorage implements LogStorage {
    protected String beanName;

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }


    /**
     * 方法发生异常是否存储
     */
    protected abstract boolean shouldStoreWhenException(Throwable ex);

    protected abstract void handleException(Throwable ex);

    protected boolean doException(Throwable ex){
        handleException(ex);        // 不管发生异常是否存储，都进行异常处理
        return shouldStoreWhenException(ex);
    }

    /**
     * 实际存储日志方法
     * @param records 日志记录对象
     */
    public final void doStore(BizLogRecord records){
        if (records.getThrowable() != null && !doException(records.getThrowable())){
            return;
        }
        store(records);
    }
}
