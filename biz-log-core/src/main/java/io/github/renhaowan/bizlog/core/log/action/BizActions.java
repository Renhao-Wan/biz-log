package io.github.renhaowan.bizlog.core.log.action;

import io.github.renhaowan.bizlog.core.log.LogConstant;
import io.github.renhaowan.bizlog.core.service.error.BizLogException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wan
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

    /**
     * 注册业务动作
     *
     * @param action 业务动作
     */
    public static void register(AbstractBizAction action) {
        AbstractBizAction old = REGISTRY.putIfAbsent(action.getCode(), action);
        if (old != null && old.equals(action)) {
            // 重复注册
            log.error("重复注册动作: {} : {}", action.getCode(), action.getDesc());
        }
        REGISTRY.put(action.getCode(), action);
    }

    /**
     * 注册业务动作
     *
     * @param actions 业务动作
     */
    public static void register(List<AbstractBizAction> actions){
        actions.forEach(BizActions::register);
    }

    /**
     * 注册业务动作
     *
     * @param code 业务动作编码
     * @param desc 业务动作描述
     */
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

    /**
     * 获取业务动作
     *
     * @param code 业务动作编码
     * @return 业务动作
     */
    public static AbstractBizAction of(String code) {
        return Optional.ofNullable(REGISTRY.get(code))
                .orElseThrow(() -> new BizLogException("未注册的业务动作: " + code));
    }

    /**
     * 获取业务动作
     *
     * @param stdBizAction 业务动作
     * @return 业务动作
     */
    public static AbstractBizAction of(StdBizAction stdBizAction) {
        return REGISTRY.get(stdBizAction.getCode());
    }
}
