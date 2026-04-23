package com.hospital.service.impl;

import com.hospital.entity.Patient;
import com.hospital.repository.PatientRepository;
import com.hospital.service.AdmissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class AdmissionServiceImpl implements AdmissionService {

    private final PatientRepository patientRepository;

    public AdmissionServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public void updateStatus(Long patientId, String status, String admissionDateStr) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        patient.setStatus(status);
        if ("Admitted".equals(status) && admissionDateStr != null && !admissionDateStr.isEmpty()) {
            patient.setAdmissionDate(Date.valueOf(admissionDateStr));
            patient.setDischargeDate(null);
        } else if ("Discharged".equals(status)) {
            patient.setDischargeDate(Date.valueOf(LocalDate.now()));
        } else {
            patient.setAdmissionDate(null);
            patient.setDischargeDate(null);
        }
        patientRepository.save(patient);
    }

    @Override
    public List<Patient> getAllPatientsWithStatus() {
        return patientRepository.findAll();
    }

    @Override
    public long countAdmitted() {
        return patientRepository.countAdmitted();
    }
}