package cn.edcheung.springskills.web.customreqlog.enums;


import java.io.Serializable;

/**
 * 默认日志操作类型枚举
 */
public enum LogTypeEnum {

    /**
     * 其它
     */
    OTHER(0, "其它"),
    /**
     * 保存
     */
    SAVE(1, "保存"),
    /**
     * 修改
     */
    UPDATE(2, "修改"),
    /**
     * 删除
     */
    DELETE(3, "删除"),
    /**
     * 查询
     */
    SELECT(4, "查询"),
    /**
     * 导入
     */
    IMPORT(5, "导入"),
    /**
     * 导出
     */
    EXPORT(6, "导出"),
    /**
     * 保存或修改
     */
    SAVE_OR_UPDATE(7, "保存或修改"),
    /**
     * 审核
     */
    AUDIT(8, "审核"),
    /**
     * 用户登陆
     */
    LOGIN(9, "用户登陆"),
    /**
     * 用户登陆
     */
    LOGOUT(10, "用户登出"),
    /**
     * 启用
     */
    ENABLE(11, "启用"),
    /**
     * 禁用
     */
    FORBIDDEN(12, "禁用");

    private final int code;
    private final String title;

    LogTypeEnum(int code, String title) {
        this.code = code;
        this.title = title;
    }

    public int getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public Serializable getValue() {
        return this.code;
    }

    @Override
    public String toString() {
        return this.title;
    }

}
