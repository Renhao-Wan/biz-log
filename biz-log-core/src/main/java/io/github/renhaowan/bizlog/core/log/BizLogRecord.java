package io.github.renhaowan.bizlog.core.log;

import io.github.renhaowan.bizlog.core.log.action.AbstractBizAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author wan
 * 业务日志记录实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BizLogRecord {

    // 动作编码（删除/新增…）
    private AbstractBizAction action;

    // 模板解析后的文本
    private String content;

    // 异常
    private Throwable throwable;

    // 任意扩展
    private Map<String, Object> extra;

    // 时间
    private LocalDateTime time;
}
