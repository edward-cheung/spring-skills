package cn.edcheung.springskills.db.persistence.entity;

import java.time.LocalDateTime;

/**
 * <p>
 * 系统操作日志表
 * </p>
 *
 * @author Edward Cheung
 * @since 2020-03-31
 */
public class CloudOperationLog {

    /**
     * 日志编号
     */
    private String id;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 操作标题
     */
    private String operationTitle;

    /**
     * 操作员工ID
     */
    private Long operationEmployeeId;

    /**
     * 操作员工姓名
     */
    private String operationEmployeeName;

    /**
     * 操作方法
     */
    private String operationMethod;

    /**
     * 请求IP
     */
    private String operationRequestIp;

    /**
     * 请求URI
     */
    private String operationRequestUri;

    /**
     * 请求参数
     */
    private String operationRequestParameter;

    /**
     * 异常信息
     */
    private String operationRequestException;

    /**
     * 请求结果：0 成功，1 失败
     */
    private Boolean operationRequestResult;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public String getOperationTitle() {
        return operationTitle;
    }

    public void setOperationTitle(String operationTitle) {
        this.operationTitle = operationTitle;
    }

    public Long getOperationEmployeeId() {
        return operationEmployeeId;
    }

    public void setOperationEmployeeId(Long operationEmployeeId) {
        this.operationEmployeeId = operationEmployeeId;
    }

    public String getOperationEmployeeName() {
        return operationEmployeeName;
    }

    public void setOperationEmployeeName(String operationEmployeeName) {
        this.operationEmployeeName = operationEmployeeName;
    }

    public String getOperationMethod() {
        return operationMethod;
    }

    public void setOperationMethod(String operationMethod) {
        this.operationMethod = operationMethod;
    }

    public String getOperationRequestIp() {
        return operationRequestIp;
    }

    public void setOperationRequestIp(String operationRequestIp) {
        this.operationRequestIp = operationRequestIp;
    }

    public String getOperationRequestUri() {
        return operationRequestUri;
    }

    public void setOperationRequestUri(String operationRequestUri) {
        this.operationRequestUri = operationRequestUri;
    }

    public String getOperationRequestParameter() {
        return operationRequestParameter;
    }

    public void setOperationRequestParameter(String operationRequestParameter) {
        this.operationRequestParameter = operationRequestParameter;
    }

    public String getOperationRequestException() {
        return operationRequestException;
    }

    public void setOperationRequestException(String operationRequestException) {
        this.operationRequestException = operationRequestException;
    }

    public Boolean getOperationRequestResult() {
        return operationRequestResult;
    }

    public void setOperationRequestResult(Boolean operationRequestResult) {
        this.operationRequestResult = operationRequestResult;
    }
}
