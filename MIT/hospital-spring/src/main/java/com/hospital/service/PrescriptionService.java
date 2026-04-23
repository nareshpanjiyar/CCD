package com.hospital.service;

import com.hospital.dto.PrescriptionDto;
import com.hospital.entity.Prescription;
import java.util.List;

public interface PrescriptionService {
    Prescription createPrescription(PrescriptionDto dto, Long doctorId);
    Prescription findByIdWithItems(Long id);
    List<Prescription> findAllWithDetails();
    List<Prescription> findByDoctorIdWithPatient(Long doctorId);
    List<Prescription> findByPatientIdWithDoctor(Long patientId);
    long countByDoctorId(Long doctorId);
    long countByPatientId(Long patientId);
    long count();
}