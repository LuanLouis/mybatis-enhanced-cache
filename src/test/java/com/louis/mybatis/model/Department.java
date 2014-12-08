package com.louis.mybatis.model;

import java.io.Serializable;

public class Department implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Short departmentId;

    private String departmentName;

    private Integer managerId;

    private Short locationId;

    public Short getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Short departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public Short getLocationId() {
        return locationId;
    }

    public void setLocationId(Short locationId) {
        this.locationId = locationId;
    }
}