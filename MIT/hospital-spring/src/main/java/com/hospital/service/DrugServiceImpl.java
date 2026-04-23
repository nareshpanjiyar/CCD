package com.hospital.service.impl;

import com.hospital.dto.DrugDto;
import com.hospital.entity.Drug;
import com.hospital.repository.DrugRepository;
import com.hospital.service.DrugService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@Service
@Transactional
public class DrugServiceImpl implements DrugService {

    private final DrugRepository drugRepository;

    public DrugServiceImpl(DrugRepository drugRepository) {
        this.drugRepository = drugRepository;
    }

    @Override
    public Drug save(DrugDto dto) {
        Drug drug = (dto.getId() != null) 
                ? drugRepository.findById(dto.getId()).orElse(new Drug())
                : new Drug();
        drug.setName(dto.getName());
        drug.setCategory(dto.getCategory());
        drug.setManufacturer(dto.getManufacturer());
        drug.setUnitPrice(BigDecimal.valueOf(dto.getUnitPrice()));
        drug.setStockQuantity(dto.getStockQuantity());
        if (dto.getExpiryDate() != null) {
            drug.setExpiryDate(Date.valueOf(dto.getExpiryDate()));
        }
        drug.setDescription(dto.getDescription());
        return drugRepository.save(drug);
    }

    @Override
    public void deleteById(Long id) {
        drugRepository.deleteById(id);
    }

    @Override
    public List<Drug> findAll() {
        return drugRepository.findAllByOrderByNameAsc();
    }

    @Override
    public List<Drug> findAllInStock() {
        return drugRepository.findAllInStock();
    }

    @Override
    public long count() {
        return drugRepository.count();
    }

    @Override
    public long countLowStock() {
        return drugRepository.countLowStock();
    }
}