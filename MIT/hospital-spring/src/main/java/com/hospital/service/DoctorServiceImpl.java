package com.hospital.service.impl;

import com.hospital.dto.DoctorDto;
import com.hospital.entity.Doctor;
import com.hospital.repository.DoctorRepository;
import com.hospital.service.DoctorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorServiceImpl(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Override
    public Doctor save(DoctorDto dto) {
        Doctor doctor = new Doctor();
        doctor.setUsername(dto.getUsername());
        doctor.setPassword(dto.getPassword());
        doctor.setName(dto.getName());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setPhone(dto.getPhone());
        doctor.setEmail(dto.getEmail());
        doctor.setLicenseNumber(dto.getLicenseNumber());
        return doctorRepository.save(doctor);
    }

    @Override
    public Doctor update(DoctorDto dto) {
        Doctor doctor = doctorRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        doctor.setUsername(dto.getUsername());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            doctor.setPassword(dto.getPassword());
        }
        doctor.setName(dto.getName());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setPhone(dto.getPhone());
        doctor.setEmail(dto.getEmail());
        doctor.setLicenseNumber(dto.getLicenseNumber());
        return doctorRepository.save(doctor);
    }

    @Override
    public void deleteById(Long id) {
        doctorRepository.deleteById(id);
    }

    @Override
    public Optional<Doctor> findById(Long id) {
        return doctorRepository.findById(id);
    }

    @Override
    public Optional<Doctor> findByUsername(String username) {
        return doctorRepository.findByUsername(username);
    }

    @Override
    public List<Doctor> findAll() {
        return doctorRepository.findAllByOrderByNameAsc();
    }

    @Override
    public long count() {
        return doctorRepository.count();
    }
}