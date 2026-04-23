package com.hospital.service.impl;

import com.hospital.dto.PatientRegistrationDto;
import com.hospital.entity.Patient;
import com.hospital.repository.PatientRepository;
import com.hospital.service.PatientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public Patient registerNewPatient(PatientRegistrationDto dto) {
        if (patientRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (patientRepository.existsByPhone(dto.getPhone())) {
            throw new IllegalArgumentException("Phone number already registered");
        }
        Patient patient = new Patient();
        patient.setUsername(dto.getUsername());
        patient.setPassword(dto.getPassword()); // In production, encode password
        patient.setName(dto.getName());
        patient.setAge(dto.getAge());
        patient.setGender(dto.getGender());
        patient.setPhone(dto.getPhone());
        patient.setEmail(dto.getEmail());
        patient.setAddress(dto.getAddress());
        patient.setBloodGroup(dto.getBloodGroup());
        patient.setStatus(dto.getStatus());
        if (dto.getAdmissionDate() != null) {
            patient.setAdmissionDate(java.sql.Date.valueOf(dto.getAdmissionDate()));
        }
        return patientRepository.save(patient);
    }

    @Override
    public Patient save(Patient patient) {
        return patientRepository.save(patient);
    }

    @Override
    public Patient update(Patient patient) {
        return patientRepository.save(patient);
    }

    @Override
    public void deleteById(Long id) {
        patientRepository.deleteById(id);
    }

    @Override
    public Optional<Patient> findById(Long id) {
        return patientRepository.findById(id);
    }

    @Override
    public Optional<Patient> findByUsername(String username) {
        return patientRepository.findByUsername(username);
    }

    @Override
    public Optional<Patient> findByPhone(String phone) {
        return patientRepository.findByPhone(phone);
    }

    @Override
    public List<Patient> findAll() {
        return patientRepository.findAllByOrderByNameAsc();
    }

    @Override
    public long count() {
        return patientRepository.count();
    }

    @Override
    public boolean existsByUsername(String username) {
        return patientRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return patientRepository.existsByPhone(phone);
    }
}