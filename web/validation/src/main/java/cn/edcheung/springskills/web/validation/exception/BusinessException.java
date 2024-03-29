package cn.edcheung.springskills.web.validation.exception;

import cn.edcheung.springskills.web.validation.enums.ErrorCode;

/**
 * 业务异常：用户能够看懂并且能够处理的异常
 */
public class BusinessException extends CustomException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造一个没有错误信息的 <code>BusinessException</code>
     */
    public BusinessException() {
        super();
    }

    /**
     * 使用指定的 Throwable 和 Throwable.toString() 作为异常信息来构造 BusinessException
     *
     * @param cause 错误原因， 通过 Throwable.getCause() 方法可以获取传入的 cause信息
     */
    public BusinessException(Throwable cause) {
        super(cause);
    }

    /**
     * 使用错误信息 message 构造 BusinessException
     *
     * @param message 错误信息
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * 使用错误码和错误信息构造 BusinessException
     *
     * @param code    错误码
     * @param message 错误信息
     */
    public BusinessException(String code, String message) {
        super(code, message);
    }

    /**
     * 使用错误信息和 Throwable 构造 BusinessException
     *
     * @param message 错误信息
     * @param cause   错误原因
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param code    错误码
     * @param message 错误信息
     * @param cause   错误原因
     */
    public BusinessException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }

    /**
     * @param errorCode ErrorCode
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * @param errorCode ErrorCode
     * @param cause     错误原因
     */
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

}
