package cn.edcheung.springskills.web.validation.bean;


import cn.edcheung.springskills.web.validation.enums.ResponseCode;

import java.io.Serializable;

/**
 * 接口返回bean封装
 */
public class ResultBean<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 时间戳
     */
    private long time = System.currentTimeMillis();
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
     * 注意:不要设置错误消息到data中(data类型不一定是字符串类型）
     * 建议使用ResultBeanBuilder
     *
     * @param responseCode 返回码
     * @param data         数据
     */
    public ResultBean(ResponseCode responseCode, T data) {
        this.code = responseCode.code();
        this.msg = responseCode.value();
        this.data = data;
    }

    public ResultBean(ResponseCode responseCode) {
        this.code = responseCode.code();
        this.msg = responseCode.value();
    }

    public ResultBean(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResultBean() {
    }

    /*public boolean getResult() {
        return ResponseCode.SUCCESS.code().equals(this.code);
    }*/

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
