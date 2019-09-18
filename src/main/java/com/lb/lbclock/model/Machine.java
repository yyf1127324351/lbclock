package com.lb.lbclock.model;

public class Machine {
    private Integer id;
    private String name; //考勤机名称
    private Integer status;//考勤机状态  0离线  1在线   2删除
    private String serialNumber; //考勤机序列号

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
}
