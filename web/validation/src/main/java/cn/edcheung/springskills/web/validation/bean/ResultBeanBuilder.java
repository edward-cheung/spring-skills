package cn.edcheung.springskills.web.validation.bean;

import cn.edcheung.springskills.web.validation.enums.ResponseCode;
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

    public static <T> ResultBean<T> success(T data) {
        return new ResultBean<>(ResponseCode.SUCCESS, data);
    }

    public static <T> ResultBean<Map> success(Map data) {
        return new ResultBean<>(ResponseCode.SUCCESS, data);
    }


    public static <T> ResultBean<T> success() {
        return new ResultBean<>(ResponseCode.SUCCESS);
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
        return buildError(ResponseCode.ERROR11006, ResponseCode.ERROR11006.value());
    }

    /**
     * 操作失败-消息
     */
    public static <T> ResultBean<T> error(String message) {
        return buildError(ResponseCode.ERROR11006, message);
    }

    /**
     * 参数不正确-请检查参数是否正确
     */
    public static <T> ResultBean<T> argumentError(String message) {
        return buildError(ResponseCode.ERROR11001, message);
    }

    /**
     * 内部错误-网络连接失败，请稍后再试
     */
    public static <T> ResultBean<T> innerError() {
        return buildError(ResponseCode.ERROR11001, ResponseCode.ERROR10000.value());
    }


    public static <T> ResultBean<T> error(ResponseCode responseCode, String message) {
        return buildError(responseCode, message);
    }

    public static <T> ResultBean<T> error(ResponseCode responseCode) {
        return buildError(responseCode, responseCode.value());
    }

    /**
     * 返回错误内容
     * 注意：如果走feign返回可能存在转换问题
     */
    public static <T> ResultBean<T> errorData(ResponseCode responseCode, T data) {
        ResultBean<T> resultBean = new ResultBean<>(responseCode);
        resultBean.setData(data);
        return resultBean;
    }

    /**
     * 错误消息
     */
    private static <T> ResultBean<T> buildError(ResponseCode responseCode, String message) {
        ResultBean<T> resultBean = new ResultBean<>(responseCode);
        if (Strings.isEmpty(message)) {
            resultBean.setMsg(responseCode.value());
        } else {
            resultBean.setMsg(message);
        }
        // 不能设置值，data可能是对象
        return resultBean;
    }
}
