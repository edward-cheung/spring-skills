package cn.edcheung.springskills.web.validation.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

public class CloudOperate implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    /**
     * 功能名称
     */
    @NotEmpty(message = "请填写功能名称")
    @Size(max = 20, message = "菜单功能最多20个字符")
    private String name;
    /**
     * 功能编码
     */
    @NotBlank(message = "请填写功能编码")
    @Size(max = 20, message = "菜单功能最多20个字符")
    private String code;
    /**
     * 功能跳转路径地址
     */
    @NotBlank(message = "请填写路径链接")
    @Size(max = 128, message = "路径链接超过限制长度")
    private String url;
    /**
     * 所属菜单code
     */
    @NotBlank(message = "请选择所属菜单")
    private String menuCode;
    /**
     * 所属菜单名称
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String menuName;
    /**
     * 状态：0禁用，1启用
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean status;
    /**
     * 系统编码
     */
    @NotBlank(message = "系统编码为空")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String cloudCode;
    /**
     * 是否删除：0否，1是
     */
    @JsonIgnore
    private Boolean isDelete;
    /**
     * 创建日期
     */
    @JsonIgnore
    private Date gmtCreate;
    /**
     * 修改日期
     */
    @JsonIgnore
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