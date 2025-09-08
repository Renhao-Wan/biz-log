package com.bizlog.core.log;

import com.bizlog.core.log.action.AbstractBizAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 业务日志记录实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BizLogRecord {
    private AbstractBizAction action;       // 动作编码（删除/新增…）
    private String content;                 // 模板解析后的文本
    private Throwable throwable;                // 异常
    private Map<String, Object> extra;      // 任意扩展
    private LocalDateTime time;             // 时间
}
