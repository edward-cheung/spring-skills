package cn.edcheung.springskills.web.validation.bean;

import cn.edcheung.springskills.web.validation.enums.ErrorCode;
import org.apache.logging.log4j.util.Strings;

import java.util.Map;

/**
 * 前端要求：
 * 1.成功，data不为空或空字符串
 * 2.失败，先取data，data没有再取message，最后网络连接失败
 * (这里后台错误消息全部放到message，data不放失败消息，部分复杂的错误信息放data，调用下面方法
 */
@SuppressWarnings("unused")
public class ResultBeanBuilder {

    public static <T> ResultBean<T> success() {
        return new ResultBean<>(ErrorCode.SUCCESS);
    }

    public static <T> ResultBean<T> success(T data) {
        return new ResultBean<>(ErrorCode.SUCCESS, data);
    }

    public static <T> ResultBean<Map> success(Map data) {
        return new ResultBean<>(ErrorCode.SUCCESS, data);
    }

    public static <T> ResultBean<T> is(Boolean result) {
        if (null == result || !result) {
            return error();
        }
        return success();
    }

    /**
     * 操作失败
     */
    public static <T> ResultBean<T> error() {
        return buildError(ErrorCode.ERROR11001, ErrorCode.ERROR11001.getMessage());
    }

    /**
     * 操作失败-消息
     */
    public static <T> ResultBean<T> error(String message) {
        return buildError(ErrorCode.ERROR11001, message);
    }

    /**
     * 操作失败-错误码，消息
     */
    public static <T> ResultBean<T> error(String code, String message) {
        return buildError(code, message);
    }

    public static <T> ResultBean<T> error(ErrorCode errorCode, String message) {
        return buildError(errorCode, message);
    }

    public static <T> ResultBean<T> error(ErrorCode errorCode) {
        return buildError(errorCode, errorCode.getMessage());
    }

    /**
     * 参数不正确-请检查参数是否正确
     */
    public static <T> ResultBean<T> argumentError(String message) {
        return buildError(ErrorCode.ERROR11003, message);
    }

    /**
     * 内部错误-网络连接失败，请稍后再试
     */
    public static <T> ResultBean<T> innerError() {
        return buildError(ErrorCode.ERROR10000, ErrorCode.ERROR10000.getCode());
    }

    /**
     * 返回错误内容
     * 注意：如果走feign返回可能存在转换问题
     */
    public static <T> ResultBean<T> errorData(ErrorCode errorCode, T data) {
        ResultBean<T> resultBean = new ResultBean<>(errorCode);
        resultBean.setData(data);
        return resultBean;
    }

    /**
     * 错误消息
     */
    private static <T> ResultBean<T> buildError(String errorCode, String message) {
        ResultBean<T> resultBean = new ResultBean<>(errorCode, message, null);
        if (Strings.isEmpty(message)) {
            resultBean.setMsg(errorCode);
        } else {
            resultBean.setMsg(message);
        }
        // 不能设置值，data可能是对象
        return resultBean;
    }

    /**
     * 错误消息
     */
    private static <T> ResultBean<T> buildError(ErrorCode errorCode, String message) {
        ResultBean<T> resultBean = new ResultBean<>(errorCode);
        if (Strings.isEmpty(message)) {
            resultBean.setMsg(errorCode.getMessage());
        } else {
            resultBean.setMsg(message);
        }
        // 不能设置值，data可能是对象
        return resultBean;
    }

}
