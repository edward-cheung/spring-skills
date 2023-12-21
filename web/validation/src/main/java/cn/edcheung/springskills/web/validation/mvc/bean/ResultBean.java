package cn.edcheung.springskills.web.validation.mvc.bean;


import cn.edcheung.springskills.web.validation.enums.ErrorCode;

import java.io.Serializable;

/**
 * 接口返回bean封装
 */
public class ResultBean<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private String code;

    /**
     * 消息
     */
    private String msg;

    /**
     * 数据
     */
    private T data;

    /**
     * 版本号
     */
    private String version = "1.0.0";

    /**
     * 时间戳
     */
    private long time = System.currentTimeMillis();

    /**
     * 注意:不要设置错误消息到data中(data类型不一定是字符串类型）
     * 建议使用ResultBeanBuilder
     *
     * @param errorCode 返回码
     * @param data      数据
     */
    public ResultBean(ErrorCode errorCode, T data) {
        this.code = errorCode.getCode();
        this.msg = errorCode.getMessage();
        this.data = data;
    }

    public ResultBean(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResultBean(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.msg = errorCode.getMessage();
    }

    public ResultBean() {
    }

    public boolean succeed() {
        return ErrorCode.SUCCESS.getCode().equals(this.code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
