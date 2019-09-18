package com.lb.lbclock.model;

import java.util.Date;

public class Clock {

    private Long id;
    private Integer operatorId;
    private Long attendanceNumber;
    private String clockInTime;
    private Integer attendanceMachineId;
    private String serialNumber;
    private Integer source;
    private Date createTime;
    private Date updateTime;
    private String sourceDescribe;
    private String clockDateTime;
    private String clockPlace;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Long getAttendanceNumber() {
        return attendanceNumber;
    }

    public void setAttendanceNumber(Long attendanceNumber) {
        this.attendanceNumber = attendanceNumber;
    }


    public String getClockInTime() {
        return clockInTime;
    }

    public void setClockInTime(String clockInTime) {
        this.clockInTime = clockInTime;
    }

    public Integer getAttendanceMachineId() {
        return attendanceMachineId;
    }

    public void setAttendanceMachineId(Integer attendanceMachineId) {
        this.attendanceMachineId = attendanceMachineId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public String getSourceDescribe() {
        return sourceDescribe;
    }

    public void setSourceDescribe(String sourceDescribe) {
        this.sourceDescribe = sourceDescribe;
    }

    public String getClockDateTime() {
        return clockDateTime;
    }

    public void setClockDateTime(String clockDateTime) {
        this.clockDateTime = clockDateTime;
    }

    public String getClockPlace() {
        return clockPlace;
    }

    public void setClockPlace(String clockPlace) {
        this.clockPlace = clockPlace;
    }
}
