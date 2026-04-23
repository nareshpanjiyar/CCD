package com.hospital.service;

import com.hospital.dto.DrugDto;
import com.hospital.entity.Drug;
import java.util.List;

public interface DrugService {
    Drug save(DrugDto dto);
    void deleteById(Long id);
    List<Drug> findAll();
    List<Drug> findAllInStock();
    long count();
    long countLowStock();
}