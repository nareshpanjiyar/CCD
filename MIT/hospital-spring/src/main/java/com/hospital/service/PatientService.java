package com.hospital.service;

import com.hospital.dto.PatientRegistrationDto;
import com.hospital.entity.Patient;
import java.util.List;
import java.util.Optional;

public interface PatientService {
    Patient registerNewPatient(PatientRegistrationDto dto);
    Patient save(Patient patient);
    Patient update(Patient patient);
    void deleteById(Long id);
    Optional<Patient> findById(Long id);
    Optional<Patient> findByUsername(String username);
    Optional<Patient> findByPhone(String phone);
    List<Patient> findAll();
    long count();
    boolean existsByUsername(String username);
    boolean existsByPhone(String phone);
}