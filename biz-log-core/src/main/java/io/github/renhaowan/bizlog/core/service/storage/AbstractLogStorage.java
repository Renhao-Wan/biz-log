package io.github.renhaowan.bizlog.core.service.storage;

import io.github.renhaowan.bizlog.core.log.BizLogRecord;
import lombok.Getter;

/**
 * @author wan
 * 抽象日志存储器，获取日志存储器的beanName
 */
@Getter
public abstract class AbstractLogStorage implements LogStorage {

    /**
     * Bean名称，用于Spring容器中获取对应的LogStorage实例
     */
    protected String beanName;

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }


    /**
     * 判断当发生异常时，是否需要存储该业务日志
     * @param ex 异常对象（Throwable）
     * @return true-需要存储；false-不需要存储
     * @author wan
     */
    protected abstract boolean shouldStoreWhenException(Throwable ex);

    /**
     * 异常处理
     * @param ex 异常对象
     * @author wan
     */
    protected abstract void handleException(Throwable ex);

    protected boolean doException(Throwable ex){
        // 不管发生异常是否存储，都进行异常处理
        handleException(ex);
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
