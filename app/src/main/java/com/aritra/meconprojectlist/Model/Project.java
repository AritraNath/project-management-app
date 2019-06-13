package com.aritra.meconprojectlist.Model;

public class Project {

    private String employeeName;
    private String clientName;
    private String tenure;
    private String totalCost;
    private String status;
    private String dateItemAdded;
    private int id;

    public Project() {

    }

    public Project(String employeeName, String clientName, String tenure, String totalCost, String status,
                   String dateItemAdded, int id) {
        this.employeeName = employeeName;
        this.clientName = clientName;
        this.tenure = tenure;
        this.totalCost = totalCost;
        this.status = status;
        this.dateItemAdded = dateItemAdded;
        this.id = id;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getTenure() {
        return tenure;
    }

    public void setTenure(String tenure) {
        this.tenure = tenure;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }

    public String getDateItemAdded() {
        return dateItemAdded;
    }

    public void setDateItemAdded(String dateItemAdded) {
        this.dateItemAdded = dateItemAdded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
