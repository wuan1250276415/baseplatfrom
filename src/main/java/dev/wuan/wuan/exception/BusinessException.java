package dev.wuan.wuan.exception;

/**
 * 业务异常类
 * 用于处理业务逻辑中的异常情况
 * 继承自RuntimeException，属于非受检异常
 *
 * @author wuan
 */
public class BusinessException extends RuntimeException {

    /**
     * 序列化ID
     */
    @java.io.Serial
    private static final long serialVersionUID = -2119302295305964305L;

    /**
     * 无参构造函数
     */
    public BusinessException() {
        super();
    }

    /**
     * 带错误信息的构造函数
     *
     * @param message 错误信息
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * 带错误信息和原因的构造函数
     *
     * @param message 错误信息
     * @param cause 异常原因
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 带原因的构造函数
     *
     * @param cause 异常原因
     */
    public BusinessException(Throwable cause) {
        super(cause);
    }

    /**
     * 完整参数的构造函数
     *
     * @param message 错误信息
     * @param cause 异常原因
     * @param enableSuppression 是否启用异常抑制
     * @param writableStackTrace 是否可写入堆栈跟踪
     */
    public BusinessException(
            String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
