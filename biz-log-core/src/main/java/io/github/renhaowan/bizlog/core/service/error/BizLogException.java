package io.github.renhaowan.bizlog.core.service.error;

/**
 * @author wan
 * 自定义日志异常
 */
public class BizLogException extends RuntimeException {

    /**
     * 构造方法
     * @param message 异常信息
     */
    public BizLogException(String message) {
        super(message);
    }

    /**
     * 构造方法
     * @param message 错误信息
     * @param cause   异常 cause
     */
    public BizLogException(String message, Throwable cause) {
        super(message, cause);
    }
}
