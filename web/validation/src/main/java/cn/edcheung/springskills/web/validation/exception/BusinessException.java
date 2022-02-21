package cn.edcheung.springskills.web.validation.exception;

import cn.edcheung.springskills.web.validation.enums.ResponseCode;

/**
 * 自定义异常
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String message;
    private ResponseCode code = ResponseCode.ERROR11006;

    public BusinessException(String message) {
        super(message);
        this.message = message;
    }

    public BusinessException(ResponseCode responseCode) {
        super(responseCode.value());
        this.code = responseCode;
        this.message = responseCode.value();
    }

    public BusinessException(ResponseCode responseCode, String message) {
        super(message);
        this.code = responseCode;
        this.message = message;
    }

    public BusinessException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    public BusinessException(ResponseCode responseCode, String message, Throwable e) {
        super(message, e);
        this.code = responseCode;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResponseCode getCode() {
        return code;
    }

    public void setCode(ResponseCode code) {
        this.code = code;
    }

}
