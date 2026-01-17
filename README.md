# biz-log-spring-boot-starter

📖 **深入了解架构原理**：[点击查看架构设计文档](docs/DESIGN_DOC.md)

![](https://img.shields.io/maven-central/v/io.github.renhao-wan/biz-log-spring-boot-starter) [![Java CI with Maven](https://github.com/Renhao-Wan/biz-log/actions/workflows/maven.yml/badge.svg)](https://github.com/Renhao-Wan/biz-log/actions/workflows/maven.yml) ![Java](https://img.shields.io/badge/Java-17+-blue.svg) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg) ![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)

## 1. 项目介绍

biz-log-spring-boot-starter是一个轻量级的业务日志记录组件，基于Spring Boot实现，提供了注解和手动调用两种方式来记录业务日志。框架支持异步记录、自定义存储方式、模板解析等功能，可以帮助开发者快速集成业务日志记录功能。

## 2. 快速开始

### 2.1 引入依赖

**版本号请替换为GitHub/Maven中央仓库的最新版本**

```xml
<dependency>
   <groupId>io.github.renhao-wan</groupId>
   <artifactId>biz-log-spring-boot-starter</artifactId>
   <version>${latest.version}</version>
</dependency>
```

### 2.2 基本使用

使用`@BizLog`注解标记需要记录日志的方法：

```java
@Service
public class UserService {

   @BizLog(
           value = "用户#{#user.name}(#{#user.id})进行了#{#action}操作",
           actionCode = StdBizAction.UPDATE_CODE,
           extras = { @ExtraValue(k = "test", v = "额外参数") }
   )
   public void updateUser(User user, String action) {
      // 业务逻辑
   }
}
```

## 3. 核心功能

### 3.1 注解方式

`@BizLog`注解支持以下参数：

| 参数名          | 类型         | 描述                            | 默认值                     |
| --------------- | ------------ | ------------------------------- | :------------------------- |
| value           | String       | 日志内容，支持模板语法          | 无                         |
| actionCode      | String       | 动作编码                        | 空字符串                   |
| async           | boolean      | 是否异步记录                    | true                       |
| storageBeanName | String       | 存储器名称                      | 空字符串（使用默认存储器） |
| extras          | ExtraValue[] | 自定义额外参数（v支持模版语法） | 空数组                     |

### 3.2 SpEL 语法速查

| 类别              | 语法示例                                       | 说明                                           |
| ----------------- | ---------------------------------------------- | ---------------------------------------------- |
| **字面量**        | `'hello'`, `123`, `true`, `null`               | 字符串、数字、布尔、null                       |
| **变量**          | `#{#user}`, `#{#ret}`, `#{#ex}`, `#{#method}`  | 通过 `EvaluationContext#setVariable` 注入      |
| **根对象**        | `#{#root}`                                     | 固定指向根对象                                 |
| **属性导航**      | `#{#user.name}`, `#{#user?.address?.city}`     | 支持 POJO + 安全导航 `?.`                      |
| **集合 & Map**    | `#{#list[0]}`, `#{#map['key']}`                | 已注册 `MapAccessor`                           |
| **集合筛选**      | `#{#users.?[age > 18]}`                        | 返回新集合                                     |
| **集合投影**      | `#{#users.![name]}`                            | 把每个元素的 name 组成新集合                   |
| **方法调用**      | `#{#user.getName()}`, `#{'abc'.toUpperCase()}` | Java 普通实例/静态方法                         |
| **静态方法**      | `#{T(java.time.LocalDateTime).now()}`          | `T(全限定类名)` 调用静态方法                   |
| **Bean 引用**     | `#{@userService.findName(#user.id)}`           | `@beanName` 语法，已配置 `BeanFactoryResolver` |
| **运算符**        | `#{1 + 2}`, `#{age > 18 ? '成年' : '未成年'}`  | 算术、关系、三目、Elvis `?:`                   |
| **字符串拼接**    | `#{'订单号：' + #order.no}`                    | 直接拼接                                       |
| **正则匹配**      | `#{#email matches '[\\w.-]+@[\\w.-]+'}`        | 返回布尔                                       |
| **内联 List/Map** | `#{ {'A','B'} }`, `#{ {name:'Tom', age:20} }`  | 快速构造集合/Map                               |

### 3.3手动调用

通过`BizLogManager`手动记录日志：

```java
@Service
public class OrderService {
   @Autowired
   private BizLogManager bizLogManager;

   public void createOrder(Order order) {
      // 业务逻辑
      // 手动记录日志
      bizLogManager.record(
              StdBizAction.CREATE_CODE,
              "创建订单：订单号1，金额10",
              true
      );
   }
}
```

## 4. 配置选项

在application.properties或application.yml中可以配置以下选项(以下均为默认值)：

### 4.1 全局配置

```yaml
biz:
   log:
      enabled: true  # 全局开关，默认为true
      storage-bean-name: consoleLogStorage  # 默认存储器名称
```

### 4.2 异步线程池配置

```yaml
biz:
   log:
      async:
         core-pool-size: 4  # 核心线程数
         max-pool-size: 8  # 最大线程数
         queue-capacity: 200  # 队列容量
         thread-name-prefix: BizLog-  # 线程名前缀
         await-termination: 30  # 优雅停机等待时间（秒）
```

### 4.3 模板解析器配置

```yaml
biz:
   log:
      parser:
         fallback-to-plain: true  # 解析失败是否回退到原文本
         spel:
            enabled: true  # 是否启用SpEL解析器
            cache-size: 100  # SpEL解析器缓存大小
            cache-time: 120  # SpEL解析器缓存时间（秒）
```

## 5. 业务拓展

### 5.1 动作编码管理

_内置动作编码_
项目提供 StdBizAction 枚举类，内置 8 个常用动作编码常量，外部调用可直接用：

- 登录（LOGIN_CODE）、创建（CREATE_CODE）、更新（UPDATE_CODE）、删除（DELETE_CODE）
- 查询（QUERY_CODE）、导出（EXPORT_CODE）、导入（IMPORT_CODE）、其他（OTHER_CODE）

_注册自定义动作编码_
当内置动作编码不足以满足需求时，可以通过 BizActions 类注册自定义动作编码：

```java
// 注册方式1：使用AbstractBizAction对象
AbstractBizAction customAction = new AbstractBizAction() {
   @Override
   public String getCode() {
      return "CUSTOM";
   }
   @Override
   public String getDesc() {
      return "自定义动作";
   }
};
BizActions.register(customAction);

// 注册方式2：使用code和desc字符串对
BizActions.register("APPROVE", "审批");

// 注册方式3：批量注册
List<AbstractBizAction> actions = new ArrayList<>();
// 添加动作...
BizActions.register(actions);
```

### 5.2 自定义线程池

项目默认使用 ThreadPoolTaskExecutor 作为异步日志的线程池，由 DefaultLogExecutorProvider 提供
通过实现 LogExecutorProvider 接口，可以自定义线程池策略。 **自定义线程池必须返回 `java.util.concurrent.Executor`**，且要与 `CompletableFuture` 兼容！！目前版本暂不推荐自定义线程池

### 5.3 自定义模板解析器

LogTemplateParser 接口定义了模板解析的核心方法
项目提供了两种内置解析器：

- SpelLogTemplateParser ：SpEL表达式解析器，优先级为1

  ```markdown
  可以通过实现`SpelLogTemplateParser.SpelExpansionContext`接口添加自定义变量到SpEL上下文中,如添加一些静态方法
  ```

- PlainTextLogTemplateParser ：纯文本解析器，优先级为 Integer.MAX_VALUE （兜底）

注册自定义解析器
可以通过实现 LogTemplateParser 接口并使用 @Order 注解指定优先级(CompositeLogTemplateParser 负责管理所有解析器，按 @Order 注解指定的优先级排序组成责任链)来添加自定义解析器：

```java
import parse.service.io.github.renhaowan.bizlog.core.LogTemplateParser;
import parse.service.io.github.renhaowan.bizlog.core.ParseContext;
import org.springframework.core.annotation.Order;

@Order(2) // 优先级介于SpEL和纯文本之间
public class MyCustomLogTemplateParser implements LogTemplateParser {
   @Override
   public boolean support(String template) {
      return template != null && template.contains("${");
   }

   @Override
   public String parse(String template, ParseContext ctx) {
      // 自定义解析逻辑
      // 例如解析${variable}格式的占位符
      String result = template;
      for (Map.Entry<String, Object> entry : ctx.getExtra().entrySet()) {
         result = result.replace("${" + entry.getKey() + "}", entry.getValue().toString());
      }
      return result;
   }
}
```

### 5.4 存储器

#### 5.4.1 自定义存储器

默认使用 `consoleLogStorage` 存储器进行存储：即打印业务日志到控制台上
继承`AbstractLogStorage`抽象类来自定义日志存储方式：

```java
@Component("customStorage")
public class CustomLogStorage extends AbstractLogStorage {

   @Override
   public void store(BizLogRecord record) {
      // 自定义存储逻辑，如存储到数据库、ES等
      // ... 具体入库代码
   }

   // 方法发生异常时是否继续进行存储(即业务方法抛错，日志是否还需要记录)
   @Override
   protected boolean shouldStoreWhenException(Throwable ex) {
      return true;
   }

   // 处理存储过程中的异常（防止日志存储失败影响业务或用于监控报警）
   @Override
   protected void handleException(Throwable ex) {
      log.error("【操作日志】发生异常: {}", ex.getMessage());
   }
}
```

使用自定义存储器：

```java
@BizLog(
        value = "用户操作日志",
        storageBeanName = "customStorage"       // 优先级大于yml配置
)
public void userOperation() {
   // 业务逻辑
}
```

在yml文件中配置全局默认存储器

```yaml
biz:
   log:
      storage-bean-name: customStorage
```

### 5.4.2 多存储器支持

支持多个存储器同时工作

```yml
# ... (原有内容)
biz:
   log:
      # ...
      # 支持配置多个存储器 Bean 名称
      storage-bean-name:
         - consoleLogStorage
         - customSLogtorage
```

**存储策略优先级**：
组件支持全局配置与注解级配置覆盖。优先级规则为：**注解指定 > 全局配置**。

如下例所示，当注解中指定了 `storageBeanName`，将忽略全局配置，仅使用注解中指定的存储器：

```java
@BizLog(
        value = "用户#{#user.name}进行了#{#action}操作",
        // 仅使用控制台和自定义存储，忽略全局配置
        storageBeanName = {"consoleLogStorage", "customLogStorage"},
        extras = {
                @ExtraValue(k = "userId", v = "#{#user.id}")
        }
)
public void updateUser(User user) { ... }
```

#### 5.4.3 控制台日志级别

默认情况下 `ConsoleLogStorage` 使用 INFO 级别输出。如需调整（例如改为 DEBUG 或 WARN），可注入配置 Bean：

```java
@Bean
public ConsoleLogStorage.ConsoleLogConfig consoleLogConfig() {
   return ConsoleLogStorage.ConsoleLogConfig.builder()
           .logLevel(ConsoleLogStorage.ConsoleLogConfig.INFO)
           .build();
}
```

### 5.5 自定义错误处理器

是对存储时发生的异常进行处理，例如：异步存储过程中线程池发生异常、解析模版时模版语法出现错误等
实现`LogErrorHandler`接口来自定义错误处理方式(同步异步记录失败时均会调用)：

```java
@Component
public class CustomLogErrorHandler implements LogErrorHandler {

   @Override
   public void onError(BizLogRecord record, Throwable ex) {
      // 自定义错误处理逻辑
      log.error("记录日志失败: {}", record, e);
   }
}
```

### 5.6 如何解决线程局部变量的传递问题

核心：存储在日志对象BizLogRecord的额外参数中
下面以SpringSecurity的SecurityContext对象进行演示

1. 手动调用

   ```java
   @Service
   public class OrderService {
       @Autowired
       private BizLogManager bizLogManager;
       
       public void createOrder(Order order) {
           // 业务逻辑
           // 手动记录日志
           SecurityContext securityContext = SecurityContextHolder.getContext();
           bizLogManager.record(
               StdBizAction.CREATE_CODE,
               "创建订单：订单号1，金额10",
               true,
               Map.of("securityContext", securityContext)   //将springContext上下文传入BizLogRecord额外变量中
           );
       }
   }
   ```

2. 注解方式

   ```java
   @Configuration
   public class BizLogConfig {
   
       /**
        * 注解方式需要实现BizLogAspect.ExtendParseContextExtraValue
        */
       @Bean
       public BizLogAspect.ExtendParseContextExtraValue extendParseContextExtraValue(){
           return () -> {
              SecurityContext securityContext = SecurityContextHolder.getContext();
               return Map.of("securityContext", securityContext);
           };
       }
   }
   ```

3. 最终使用

   ```java
   @Component("customStorage")
   public class CustomLogStorage extends AbstractLogStorage {
   
       @Override
       public void store(BizLogRecord record) {
           // 在BizLogRecord中获取之前存的线程局部变量
           SecurityContext securityContext = bizLogRecord.getExtra().get("securityContext");
           SecurityContextHolder.setContext(securityContext);
           // 继续进行其它业务操作
       }
   }
   ```



## 6. 完整示例

### 6.1 注解方式完整示例

```java
@RestController
@RequestMapping("/api/user")
public class UserController {

   @Autowired
   private UserService userService;

   @PostMapping("/operate")
   public ResponseEntity<String> operateUser(@RequestBody UserOperationRequest request) {
      userService.operateUser(request.getUser(), request.getAction());
      return ResponseEntity.ok("操作成功");
   }
}

@Service
public class UserService {

   @BizLog(
           value = "用户#{#user.name}(#{#user.id})进行了#{#action}操作",
           actionCode = StdBizAction.UPDATE_CODE,
           async = true,
           extras = {
                   @ExtraValue(k = "userName", v = "#{#user.name}"),
                   @ExtraValue(k = "userId", v = "#{#user.id}"),
           }
   )
   public void updateUser(User user, String action) {
      // 业务逻辑
      System.out.println("执行用户操作: " + action + " for user: " + user.getName());
   }
}
```

### 6.2 手动调用完整示例

```java
@Service
public class OrderService {

   @Autowired
   private BizLogManager bizLogManager;

   public Order createOrder(OrderDTO orderDTO) {
      // 创建订单业务逻辑
      Order order = new Order();
      order.setId(UUID.randomUUID().toString());
      order.setOrderNo(orderDTO.getOrderNo());
      order.setAmount(orderDTO.getAmount());
      order.setCreateTime(LocalDateTime.now());

      // 手动记录日志
      bizLogManager.record(
              StdBizAction.CREATE_CODE,
              "创建订单：订单号{orderNo}，金额{amount}".replace("{orderNo}", order.getOrderNo()).replace("{amount}", order.getAmount().toString()),
              true
      );

      return order;
   }
}
```

## 7. 注意事项

1. 确保Spring Boot版本与starter兼容（当前支持Spring Boot 3+）。
2. 模板语法支持SpEL表达式，可以通过`#{#表达式}`来引用方法参数和返回值。
3. 异步记录日志时，确保线程池配置合理，避免线程资源耗尽。
4. 对响应速度要求较高的场景下建议使用手动调用的方式记录日志
5. 自定义存储器时，需要将实现类注册为Spring Bean，并指定正确的bean名称。

## 8. 其它

### 8.1 如何让IDEA识别SpEL表达式？

1. 第一步：下载插件 _SpEL Assistant_

2. 第二步：把下面这段内容直接放到当前工程`src/main/resources/spel-extension.json`
   保存后重启 IDEA，即可让  _SpEL Assistant_  对 `@BizLog` 注解里的 `value` 和 `bizId` 字段实现高亮与代码提示。

   ```json
   {
     "annotation.log.io.github.renhaowan.bizlog.core.BizLog@value": {
       "prefix": "#{",
       "suffix": "}",
       "method": {
         "parameters": true
       },
       "variables": [
         { "name": "ret",  "type": "java.lang.Object" },
         { "name": "ex",   "type": "java.lang.Throwable" },
         { "name": "method", "type": "java.lang.reflect.Method" }
       ]
     },
     "annotation.log.io.github.renhaowan.bizlog.core.BizLog@bizId": {
       "prefix": "#{",
       "suffix": "}",
       "method": {
         "parameters": true
       },
       "variables": [
         { "name": "ret",  "type": "java.lang.Object" },
         { "name": "ex",   "type": "java.lang.Throwable" },
         { "name": "method", "type": "java.lang.reflect.Method" }
       ]
     },
     "annotation.log.io.github.renhaowan.bizlog.core.ExtraValue@v": {
       "prefix": "#{",
       "suffix": "}",
       "method": {
         "parameters": true
       },
       "variables": [
         { "name": "ret",  "type": "java.lang.Object" },
         { "name": "ex",   "type": "java.lang.Throwable" },
         { "name": "method", "type": "java.lang.reflect.Method" }
       ]
     }
   }
   ```

3. 添加`src/main/resources/spel-extension.json`文件后建议使用模版语法时都加`#{}`边界，否则插件会报错（不影响程序的正常执行）

