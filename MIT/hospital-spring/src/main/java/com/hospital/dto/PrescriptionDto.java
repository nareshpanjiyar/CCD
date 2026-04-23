package com.hospital.dto;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


public class PrescriptionDto {

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<PrescriptionItemDto> getItems() {
        return items;
    }

    public void setItems(List<PrescriptionItemDto> items) {
        this.items = items;
    }

    @NotNull(message = "Patient is required")
    private Long patientId;

    private Long appointmentId; // optional

    private String diagnosis;
    private String notes;

    private List<PrescriptionItemDto> items = new ArrayList<>();
}