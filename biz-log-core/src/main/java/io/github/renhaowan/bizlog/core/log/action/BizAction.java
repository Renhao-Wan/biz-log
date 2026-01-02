package io.github.renhaowan.bizlog.core.log.action;

/**
 * @author wan
 * 抽象业务操作: 主要用于定义业务操作的编码和描述规范
 */
public interface BizAction {
    /**
     * 获取业务操作的编码
     * @return 业务操作的编码
     */
    String getCode();

    /**
     * 获取业务操作的描述
     * @return 业务操作的描述
     */
    String getDesc();
}
