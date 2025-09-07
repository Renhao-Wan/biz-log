package com.bizlog.core.log.action;

/**
 * 抽象业务操作: 主要用于定义业务操作的编码和描述规范
 */
public interface BizAction {
    String getCode();
    String getDesc();
}
