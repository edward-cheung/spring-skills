package cn.edcheung.springskills.web.validation.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * 嵌套校验：
 * 如果需要校验的参数对象中还嵌套有一个对象属性，而该嵌套的对象属性也需要校验，那么就需要在该对象属性上增加@Valid注解
 * <p>
 * 分组校验：
 * 在参数类中需要校验的属性上，在注解中添加groups属性，比如 @NotNull(groups = ToUpdate.class, message = "id不能为空")
 * 然后在controller的方法中，在@Validated注解里指定哪种场景即可，没有指定就代表采用Default.class，采用其他分组就需要显示指定
 */
public class CloudMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单功能id
     */
    private Long id;

    /**
     * 菜单名称
     */
    @NotEmpty(message = "请填写菜单名称")
    @Size(max = 20, message = "菜单名称最多20个字符")
    private String name;

    /**
     * 菜单编码
     */
    @NotBlank(message = "请填写菜单编码")
    @Size(max = 20, message = "菜单编码最多20个字符")
    private String code;

    /**
     * 图标
     */
    @JsonIgnore
    private String img;

    /**
     * 菜单地址
     */
    @Size(max = 128, message = "跳转URL超过限制长度")
    private String url;

    /**
     * 菜单父级Id
     */
    private Long pid;

    /**
     * 菜单父级名称
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long parentName;

    /**
     * 菜单等级，分一级菜单和二级菜单
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer level;

    /**
     * 顺序
     */
    @JsonIgnore
    private Integer orders;

    /**
     * 状态：0禁用，1启用
     */
    @JsonIgnore
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getOrders() {
        return orders;
    }

    public void setOrders(Integer orders) {
        this.orders = orders;
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

    public Long getParentName() {
        return parentName;
    }

    public void setParentName(Long parentName) {
        this.parentName = parentName;
    }
}