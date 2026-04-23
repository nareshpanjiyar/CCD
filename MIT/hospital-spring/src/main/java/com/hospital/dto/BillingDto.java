package com.hospital.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;


public class BillingDto {

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getPatientId() {
        return patientId;
    }
    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public LocalDate getBillingDate() {
        return billingDate;
    }
    public void setBillingDate(LocalDate billingDate) {
        this.billingDate = billingDate;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    private Long id;

    @NotNull(message = "Patient is required")
    private Long patientId;

    @Positive(message = "Amount must be positive")
    private double amount;

    @NotNull(message = "Billing date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate billingDate;

    private String description;
    private String status = "Pending";
}