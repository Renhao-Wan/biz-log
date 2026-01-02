package io.github.renhaowan.bizlog.core.service;

import io.github.renhaowan.bizlog.core.log.annotation.BizLog;
import io.github.renhaowan.bizlog.core.log.annotation.ExtraValue;
import io.github.renhaowan.bizlog.core.service.parse.ParseContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author wan
 * 日志切面
 */
@Aspect
public class BizLogAspect {
    private final BizLogManager bizLogManager;
    private final ParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();
    private final ExtendParseContextExtraValue extendParseContextExtraValue;

    /**
     * 构造函数
     * @param bizLogManager 日志管理器
     * @param extendParseContextExtraValue 扩展解析上下文额外参数
     * @author wan
     */
    public BizLogAspect(BizLogManager bizLogManager, ExtendParseContextExtraValue extendParseContextExtraValue) {
        this.bizLogManager = bizLogManager;
        this.extendParseContextExtraValue = extendParseContextExtraValue;
    }

    /**
     * AOP环绕通知，用于拦截标记了@BizLog的方法，执行日志记录逻辑
     * @param pjp 切面连接点，用于获取方法信息和执行原方法
     * @param bizLog 业务日志注解，获取注解配置的参数
     * @return 原方法的执行结果
     * @throws Throwable 原方法执行过程中抛出的所有异常
     * @author wan
     */
    @Around("@annotation(bizLog)")
    public Object around(ProceedingJoinPoint pjp, BizLog bizLog) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object retVal = null;
        Throwable thrown = null;

        try {
            // 执行业务
            retVal = pjp.proceed();
        } catch (Throwable ex) {
            thrown = ex;
            // 继续抛出
            throw ex;
        } finally {
            // 无论成功还是异常都会记录
            Map<String, Object> argsMap = this.buildExtraMap(method, pjp.getArgs(), nameDiscoverer);
            Map<String, Object> extra = new HashMap<>();
            extra.put("actionCode", bizLog.actionCode());
            // 扩展参数（接口）
            extendExtraValue(extra);
            // 扩展参数（ExtraValue注解）
            extendExtraValue(bizLog.extras(), extra);

            ParseContext ctx = ParseContext.builder()
                    .method(method)
                    .args(argsMap)
                    .retValue(retVal)
                    .thrown(thrown)
                    .extra(extra)
                    .build();
            bizLogManager.record(bizLog.value(), ctx, bizLog.async(), bizLog.storageBeanName());
        }
        return retVal;
    }

    // 构建参数map<paramName, paramValue>
    private Map<String, Object> buildExtraMap(Method method,
                                              Object[] args,
                                              ParameterNameDiscoverer nameDiscoverer) {
        Map<String, Object> map = new HashMap<>();
        String[] paramNames = nameDiscoverer.getParameterNames(method);
        for (int i = 0; i < paramNames.length; i++) {
            map.put(paramNames[i], args[i]);
        }
        return map;
    }

    // 扩展参数(接口)
    private void extendExtraValue(Map<String, Object> extra){
        Optional.ofNullable(extendParseContextExtraValue.getExtraValue())
                .ifPresent(extra::putAll);
    }

    // 扩展参数(ExtraValue注解)
    private void extendExtraValue(ExtraValue[] extras, Map<String, Object> extra){
        for (ExtraValue extraValue : extras) {
            extra.put(extraValue.k(), extraValue.v());
        }
    }

    /**
     * 扩展解析上下文额外参数
     */
    public interface ExtendParseContextExtraValue {
        Map<String, Object> getExtraValue();
    }
}
