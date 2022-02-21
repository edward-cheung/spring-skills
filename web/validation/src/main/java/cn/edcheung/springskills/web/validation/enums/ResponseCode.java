package cn.edcheung.springskills.web.validation.enums;

/**
 * 错误码
 */
public enum ResponseCode {

    ERROR10000("10000", "系统异常，请稍后再试"),
    ERROR10001("10001", "网络连接超时，请稍后再试"),
    ERROR10002("10002", "系统环境异常，请联系管理员"),
    ERROR10003("10003", "系统内部错误，请联系管理员"),

    ERROR11001("11001", "请检查参数是否正确"),
    ERROR11002("11002", "请求参数不能为空"),
    ERROR11003("11003", "记录已经存在"),
    ERROR11004("11004", "记录不存在"),
    ERROR11005("11005", "数据不能修改"),
    ERROR11006("11006", "操作失败"),
    ERROR11007("11007", "权限不足"),
    ERROR11008("11008", "登陆过期"),
    ERROR11009("11009", "id不能为空"),
    ERROR11010("11010", "数据越权操作"),
    ERROR11011("11011", "角色不存在"),
    ERROR11012("11012", "请先初始化菜单数据"),
    ERROR11013("11013", "不能对当前登录账号进行操作"),
    ERROR11014("11014", "应用未找到或已被禁用"),

    ERROR12001("20001", "还未配置服务商"),

    SUCCESS("0", "成功");

    /**
     * 编码
     **/
    private final String code;

    /**
     * 描述
     **/
    private final String value;

    ResponseCode(final String code, final String desc) {
        this.code = code;
        this.value = desc;
    }

    /**
     * 通过编码获取描述
     *
     * @param code 编码
     * @return 描述
     */
    public static ResponseCode fromValue(String code) {
        if (code == null || code.length() == 0) {
            return null;
        }

        ResponseCode[] its = ResponseCode.values();
        for (ResponseCode it : its) {
            if (it.code().equals(code)) {
                return it;
            }
        }
        return SUCCESS;
    }

    public String code() {
        return this.code;
    }

    public String value() {
        return this.value;
    }
}
