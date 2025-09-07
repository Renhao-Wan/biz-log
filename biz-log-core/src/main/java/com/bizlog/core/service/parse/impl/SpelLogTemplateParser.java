package com.bizlog.core.service.parse.impl;

import com.bizlog.core.log.BizLogProperties;
import com.bizlog.core.log.LogConstant;
import com.bizlog.core.service.error.BizLogException;
import com.bizlog.core.service.parse.LogTemplateParser;
import com.bizlog.core.service.parse.ParseContext;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MapAccessor;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

/**
 * SpEL 解析器（starter 默认）
 */
@Order(1)
@Slf4j(topic = LogConstant.BIZ_LOG)
public class SpelLogTemplateParser implements LogTemplateParser {

    // 二次缓存：模板（template) -> Expression（跳过解析）
    // 缓存的是“模板 → AST”，不是“模板 → 结果”。
    // 结果值始终由当前 EvaluationContext 决定，与上一次无关。
    // 不通过ParseContext->EvaluationContext缓存：避免脏读, EvaluationContext 每次都重新构建（很轻），真正昂贵的是 Expression 的 AST。
    private final Cache<String, Expression> exprCache;

    // SpEL 引擎 ：// 擎级缓存：Expression -> 字节码
    // 传 null 表示“使用当前线程上下文类加载器”，这是官方允许的默认值。
    private final SpelExpressionParser parser = new SpelExpressionParser(
            new SpelParserConfiguration(SpelCompilerMode.IMMEDIATE, null)
    );
    //指定模板定界符：#{ }
    private final TemplateParserContext spelCtx = new TemplateParserContext("#{", "}");

    private final ApplicationContext applicationContext;
    private final SpelExpansionContext spelExpansionContext;

    public SpelLogTemplateParser(ApplicationContext applicationContext, BizLogProperties prop, SpelExpansionContext spelExpansionContext) {
        this.applicationContext = applicationContext;
        this.spelExpansionContext = spelExpansionContext;
        BizLogProperties.Parser.Spel spel = prop.getParser().getSpel();
        this.exprCache = Caffeine.newBuilder()
                .maximumSize(spel.getCacheSize())
                .expireAfterAccess(spel.getCacheTime(), TimeUnit.SECONDS)
                .build();
    }

    @Override
    public boolean support(String template) {
        return template != null && template.contains("#{");
    }

    @Override
    public String parse(String template, ParseContext ctx) {
        Expression expression = exprCache.get(
                template,
                t -> parser.parseExpression(t, spelCtx)   // 只会在第一次执行
        );
        EvaluationContext spelEvalCtx = buildEvaluationContext(ctx);
        Assert.notNull(expression, "SpEL Expression cannot be null");
        try {
            return expression.getValue(spelEvalCtx, String.class);
        } catch (EvaluationException e) {
            log.error("SpEL解析异常: " + e.getMessage(), e);
            throw new BizLogException("SpEL解析异常: " + e.getMessage(), e);
        }
    }

    // 构建 SpEL 运行时上下文
    private EvaluationContext buildEvaluationContext(ParseContext ctx) {
        StandardEvaluationContext ec = new StandardEvaluationContext();

        // 把方法参数变成 root object，可以用 #root.args[0] 或 #course
//        ec.setRootObject(new MethodArgsRoot(ctx.getArgs()));

        // 常用变量直接绑定，模板里直接写 #ret、#ex、#method、#args[0] 、#classId...
        ctx.getArgs().forEach(ec::setVariable);            // 方法参数：参数名，参数值
        ec.setVariable("ret",   ctx.getRetValue());  // 返回值
        ec.setVariable("ex",    ctx.getThrown());    // 异常
        ec.setVariable("method", ctx.getMethod());   // 方法

        // 让 SpEL 支持 Map、List 下标访问，例如 #map['key']
        ec.addPropertyAccessor(new MapAccessor());
        //让 SpEL 识别 POJO。
        ec.addPropertyAccessor(new ReflectivePropertyAccessor());

        /* 支持 @beanName 语法：#{@userService.findName(1)} */
        ec.setBeanResolver(new BeanFactoryResolver(applicationContext));

        // 还可以注册工具函数：#{#mdc('traceId')}
        spelExpansionContext.expand(ec);                // 外部拓展

        return ec;
    }

    /**
     * 拓展 SpEL 上下文 : 外界可添加自定义变量
     */
    public interface SpelExpansionContext {
        void expand(StandardEvaluationContext ec);
    }
}
