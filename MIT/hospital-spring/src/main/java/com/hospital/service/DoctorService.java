package com.hospital.service;

import com.hospital.dto.DoctorDto;
import com.hospital.entity.Doctor;
import java.util.List;
import java.util.Optional;

public interface DoctorService {
    Doctor save(DoctorDto dto);
    Doctor update(DoctorDto dto);
    void deleteById(Long id);
    Optional<Doctor> findById(Long id);
    Optional<Doctor> findByUsername(String username);
    List<Doctor> findAll();
    long count();
}