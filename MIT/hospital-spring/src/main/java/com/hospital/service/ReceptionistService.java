package com.hospital.service;

import com.hospital.dto.ReceptionistDto;
import com.hospital.entity.Receptionist;
import java.util.List;
import java.util.Optional;

public interface ReceptionistService {
    Receptionist save(ReceptionistDto dto);
    void deleteById(Long id);
    Optional<Receptionist> findById(Long id);
    Optional<Receptionist> findByUsername(String username);
    List<Receptionist> findAll();
    long count();
}