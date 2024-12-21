package com.model;

import java.util.Date;

public class Payment {
    private String paymentId;
    private String patientId;
    private String patientName;
    private String patientType;
    private Date paymentDate;
    private String paymentMode;
    private double totalBill;

    public Payment(String paymentId, String patientId, String patientName, String patientType, Date paymentDate, String paymentMode, double totalBill) {
        this.paymentId = paymentId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientType = patientType;
        this.paymentDate = paymentDate;
        this.paymentMode = paymentMode;
        this.totalBill = totalBill;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientType() {
        return patientType;
    }

    public void setPatientType(String patientType) {
        this.patientType = patientType;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public double getTotalBill() {
        return totalBill;
    }

    public void setTotalBill(double totalBill) {
        this.totalBill = totalBill;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", patientId='" + patientId + '\'' +
                ", patientName='" + patientName + '\'' +
                ", patientType='" + patientType + '\'' +
                ", paymentDate=" + paymentDate +
                ", paymentMode='" + paymentMode + '\'' +
                ", totalBill=" + totalBill +
                '}';
    }
}
