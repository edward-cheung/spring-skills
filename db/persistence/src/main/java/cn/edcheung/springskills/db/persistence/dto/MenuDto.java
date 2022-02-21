package cn.edcheung.springskills.db.persistence.dto;

import java.io.Serializable;

/**
 * Description MenuDto
 *
 * @author Edward Cheung
 * @date 2020/11/19
 * @since JDK 1.8
 */
public class MenuDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String code;

    private Boolean status;

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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
