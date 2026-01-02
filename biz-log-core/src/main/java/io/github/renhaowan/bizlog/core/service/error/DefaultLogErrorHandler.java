package io.github.renhaowan.bizlog.core.service.error;

import io.github.renhaowan.bizlog.core.log.BizLogRecord;
import io.github.renhaowan.bizlog.core.service.storage.impl.ConsoleLogStorage;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wan
 * 默认的错误处理
 */
@RequiredArgsConstructor
public class DefaultLogErrorHandler implements LogErrorHandler {

    private final ConsoleLogStorage consoleLogStorage;

    /**
     * 错误处理
     *
     * @param records 日志记录
     * @param ex      错误
     */
    @Override
    public void onError(BizLogRecord records, Throwable ex) {
        Map<String, Object> map = new HashMap<>();
        map.put("error", ex.getMessage());
        map.put("异常处理器", this.getClass().getSimpleName());
        records.setExtra(map);
        consoleLogStorage.doStore(records);
        throw new BizLogException(ex.getMessage(), ex);
    }
}
