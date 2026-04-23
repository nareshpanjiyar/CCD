package com.hospital.service;

import com.hospital.entity.Patient;
import java.util.List;

public interface AdmissionService {
    void updateStatus(Long patientId, String status, String admissionDate);
    List<Patient> getAllPatientsWithStatus();
    long countAdmitted();
}