package io.github.renhaowan.bizlog.core.service.parse.impl;

import io.github.renhaowan.bizlog.core.log.LogConstant;
import io.github.renhaowan.bizlog.core.service.error.BizLogException;
import io.github.renhaowan.bizlog.core.service.parse.LogTemplateParser;
import io.github.renhaowan.bizlog.core.service.parse.ParseContext;
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

/**
 * @author wan
 * SpEL 解析器（starter 默认）
 */
@Order(1)
@Slf4j(topic = LogConstant.BIZ_LOG)
public class SpelLogTemplateParser implements LogTemplateParser {


    // SpEL 引擎 ：采用混合模式
    // 传 null 表示“使用当前线程上下文类加载器”。
    private final SpelExpressionParser parser = new SpelExpressionParser(
            new SpelParserConfiguration(SpelCompilerMode.MIXED, null)
    );
    //指定模板定界符：#{ }
    private final TemplateParserContext spelCtx = new TemplateParserContext("#{", "}");

    private final ApplicationContext applicationContext;
    private final SpelExpansionContext spelExpansionContext;


    /**
     * 构建Spel日志模板解析器，提供Spel表达式解析能力
     * @param applicationContext Spring 上下文
     * @param spelExpansionContext 拓展上下文
     */
    public SpelLogTemplateParser(ApplicationContext applicationContext, SpelExpansionContext spelExpansionContext) {
        this.applicationContext = applicationContext;
        this.spelExpansionContext = spelExpansionContext;
    }

    /**
     * 是否支持当前语法
     */
    @Override
    public boolean support(String template) {
        return template != null && template.contains("#{");
    }

    /**
     * 解析模板
     *
     * @param template 模板
     * @param ctx      运行时上下文
     * @return 解析后的纯文本
     */
    @Override
    public String parse(String template, ParseContext ctx) {
        Expression expression = parser.parseExpression(template, spelCtx);
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
     * @author wan
     * 拓展 SpEL 上下文 : 外界可添加自定义变量
     */
    public interface SpelExpansionContext {
        void expand(StandardEvaluationContext ec);
    }
}
