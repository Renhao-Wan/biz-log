package com.bizlog.core.service;

import com.bizlog.core.log.annotation.BizLog;
import com.bizlog.core.log.annotation.ExtraValue;
import com.bizlog.core.service.parse.ParseContext;
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
 * 日志切面
 */
@Aspect
public class BizLogAspect {
    private final BizLogManager bizLogManager;
    private final ParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();
    private final ExtendParseContextExtraValue extendParseContextExtraValue;

    public BizLogAspect(BizLogManager bizLogManager, ExtendParseContextExtraValue extendParseContextExtraValue) {
        this.bizLogManager = bizLogManager;
        this.extendParseContextExtraValue = extendParseContextExtraValue;
    }

    // 记录日志切面
    @Around("@annotation(bizLog)")
    public Object around(ProceedingJoinPoint pjp, BizLog bizLog) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object retVal = null;
        Throwable thrown = null;

        try {
            retVal = pjp.proceed(); // 执行业务
        } catch (Throwable ex) {
            thrown = ex;
            throw ex;               // 继续抛出
        } finally {
            // 无论成功还是异常都会记录
            Map<String, Object> argsMap = this.buildExtraMap(method, pjp.getArgs(), nameDiscoverer);
            Map<String, Object> extra = new HashMap<>();
            extra.put("actionCode", bizLog.actionCode());
            extra.put("bizId", bizLog.bizId());
            extendExtraValue(extra);         // 扩展参数（接口）
            extendExtraValue(bizLog.extras(), extra);   // 扩展参数（ExtraValue注解）

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
