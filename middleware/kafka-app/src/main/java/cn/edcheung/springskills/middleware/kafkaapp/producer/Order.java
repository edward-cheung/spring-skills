package cn.edcheung.springskills.middleware.kafkaapp.producer;

import java.io.Serializable;

public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String desc;
    private Double amount;

    public Order(Long id, String desc, Double amount) {
        this.id = id;
        this.desc = desc;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
