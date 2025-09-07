package com.bizlog.core.log.action;

import com.bizlog.core.log.LogConstant;
import com.bizlog.core.service.error.BizLogException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 业务操作统一访问入口
 **/
@Slf4j(topic = LogConstant.BIZ_LOG)
@SuppressWarnings("unused")
public final class BizActions {
    private static final Map<String, AbstractBizAction> REGISTRY = new ConcurrentHashMap<>();

    private BizActions() {
        throw new BizLogException("BizActions禁止实例化");
    }

    static {
        // 注册内置动作
        for (StdBizAction action : StdBizAction.values()) {
            BizActions.register(action.getCode(), action.getDesc());
        }
    }

    public static void register(AbstractBizAction action) {
        AbstractBizAction old = REGISTRY.putIfAbsent(action.getCode(), action);
        if (old != null && old.equals(action)) {
            // 重复注册
            log.error("重复注册动作: {} : {}", action.getCode(), action.getDesc());
        }
        REGISTRY.put(action.getCode(), action);
    }

    public static void register(List<AbstractBizAction> actions){
        actions.forEach(BizActions::register);
    }

    public static void register(String code, String desc) {
        register(new AbstractBizAction() {
            @Override
            public String getCode() {
                return code;
            }

            @Override
            public String getDesc() {
                return desc;
            }
        });
    }

    public static AbstractBizAction of(String code) {
        return Optional.ofNullable(REGISTRY.get(code))
                .orElseThrow(() -> new BizLogException("未注册的业务动作: " + code));
    }

    public static AbstractBizAction of(StdBizAction stdBizAction) {
        return REGISTRY.get(stdBizAction.getCode());
    }
}
