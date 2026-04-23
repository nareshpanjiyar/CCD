package com.hospital.service.impl;

import com.hospital.dto.ReceptionistDto;
import com.hospital.entity.Receptionist;
import com.hospital.repository.ReceptionistRepository;
import com.hospital.service.ReceptionistService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReceptionistServiceImpl implements ReceptionistService {

    private final ReceptionistRepository receptionistRepository;

    public ReceptionistServiceImpl(ReceptionistRepository receptionistRepository) {
        this.receptionistRepository = receptionistRepository;
    }

    @Override
    public Receptionist save(ReceptionistDto dto) {
        Receptionist receptionist = (dto.getId() != null)
                ? receptionistRepository.findById(dto.getId()).orElse(new Receptionist())
                : new Receptionist();
        receptionist.setUsername(dto.getUsername());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            receptionist.setPassword(dto.getPassword());
        }
        receptionist.setName(dto.getName());
        receptionist.setPhone(dto.getPhone());
        receptionist.setEmail(dto.getEmail());
        return receptionistRepository.save(receptionist);
    }

    @Override
    public void deleteById(Long id) {
        receptionistRepository.deleteById(id);
    }

    @Override
    public Optional<Receptionist> findById(Long id) {
        return receptionistRepository.findById(id);
    }

    @Override
    public Optional<Receptionist> findByUsername(String username) {
        return receptionistRepository.findByUsername(username);
    }

    @Override
    public List<Receptionist> findAll() {
        return receptionistRepository.findAll();
    }

    @Override
    public long count() {
        return receptionistRepository.count();
    }
}