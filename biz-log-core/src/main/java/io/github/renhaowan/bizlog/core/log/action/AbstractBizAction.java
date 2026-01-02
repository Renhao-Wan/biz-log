package io.github.renhaowan.bizlog.core.log.action;

/**
 * @author wan
 * 抽象业务行为，继承该抽象类，实现业务行为，主要重写了getCode()和getDesc()方法
 */
public abstract class AbstractBizAction implements BizAction {
    @Override
    public int hashCode() {
        return getCode().hashCode() + getDesc().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BizAction
                && getCode().equals(((BizAction) obj).getCode())
                && getDesc().equals(((BizAction) obj).getDesc());
    }
}
