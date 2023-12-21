package cn.edcheung.springskills.db.persistence.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 菜单操作表
 * </p>
 *
 * @author Edward Cheung
 * @since 2020-03-31
 */
public class CloudOperate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单功能id
     */
    private Long id;

    /**
     * 功能名称
     */
    private String name;

    /**
     * 功能编码
     */
    private String code;

    /**
     * 功能跳转路径地址
     */
    private String url;

    /**
     * 所属菜单code
     */
    private String menuCode;

    /**
     * 所属菜单名称
     */
    private String menuName;

    /**
     * 状态：0禁用，1启用
     */
    private Boolean status;

    /**
     * 系统编码
     */
    private String cloudCode;

    /**
     * 是否删除：0否，1是
     */
    private Boolean isDelete;

    /**
     * 创建日期
     */
    private Date gmtCreate;

    /**
     * 修改日期
     */
    private Date gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getCloudCode() {
        return cloudCode;
    }

    public void setCloudCode(String cloudCode) {
        this.cloudCode = cloudCode;
    }

    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }
}