package io.github.renhaowan.bizlog.core.service.error;

/**
 * 自定义日志异常
 */
public class BizLogException extends RuntimeException {
    public BizLogException(String message) {
        super(message);
    }

    public BizLogException(String message, Throwable cause) {
        super(message, cause);
    }
}
