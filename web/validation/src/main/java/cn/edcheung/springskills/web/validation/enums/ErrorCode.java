package cn.edcheung.springskills.web.validation.enums;

/**
 * 错误码
 */
public enum ErrorCode {

    /**
     * 系统异常，请稍后再试
     */
    ERROR10000("10000", "系统异常，请稍后再试"),
    /**
     * 系统异常，请稍后再试
     */
    ERROR10001("10001", "网络连接超时，请稍后再试"),
    /**
     * 系统异常，请稍后再试
     */
    ERROR10002("10002", "系统环境异常，请联系管理员"),
    /**
     * 系统异常，请稍后再试
     */
    ERROR10003("10003", "系统内部错误，请联系管理员"),

    /**
     * 系统异常，请稍后再试
     */
    ERROR11001("11001", "请检查参数是否正确"),
    /**
     * 系统异常，请稍后再试
     */
    ERROR11002("11002", "权限不足"),
    /**
     * 系统异常，请稍后再试
     */
    ERROR11003("11003", "登陆过期"),
    /**
     * 系统异常，请稍后再试
     */
    ERROR11004("11004", "数据越权操作"),
    /**
     * 系统异常，请稍后再试
     */
    ERROR11005("11005", "角色不存在"),

    /**
     * 成功
     */
    SUCCESS("00000", "成功");

    /**
     * 错误码
     */
    private String code;
    /**
     * 错误信息
     */
    private String message;

    ErrorCode(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 通过编码获取描述
     *
     * @param code 编码
     * @return 描述
     */
    public static ErrorCode fromValue(String code) {
        if (code != null && code.length() > 0) {
            ErrorCode[] errorCodes = ErrorCode.values();
            for (ErrorCode errorCode : errorCodes) {
                if (code.equals(errorCode.getCode())) {
                    return errorCode;
                }
            }
        }
        return null;
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置错误码
     *
     * @param code 错误码
     * @return 返回当前枚举
     */
    public ErrorCode setCode(String code) {
        this.code = code;
        return this;
    }

    /**
     * 获取错误信息
     *
     * @return 错误信息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置错误信息
     *
     * @param message 错误信息
     * @return 返回当前枚举
     */
    public ErrorCode setMessage(String message) {
        this.message = message;
        return this;
    }
}
