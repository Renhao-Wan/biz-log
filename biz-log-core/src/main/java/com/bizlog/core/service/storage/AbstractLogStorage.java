package com.bizlog.core.service.storage;

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
}
