package com.hospital.service.impl;

import com.hospital.dto.BillingDto;
import com.hospital.entity.Billing;
import com.hospital.entity.Patient;
import com.hospital.repository.BillingRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.service.BillingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@Service
@Transactional
public class BillingServiceImpl implements BillingService {

    private final BillingRepository billingRepository;
    private final PatientRepository patientRepository;

    public BillingServiceImpl(BillingRepository billingRepository, PatientRepository patientRepository) {
        this.billingRepository = billingRepository;
        this.patientRepository = patientRepository;
    }

    @Override
    public Billing generateBill(BillingDto dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        Billing bill = new Billing();
        bill.setPatient(patient);
        bill.setAmount(BigDecimal.valueOf(dto.getAmount()));
        bill.setBillingDate(Date.valueOf(dto.getBillingDate()));
        bill.setDescription(dto.getDescription());
        bill.setStatus(dto.getStatus());
        return billingRepository.save(bill);
    }

    @Override
    public Billing save(BillingDto dto) {
        Billing bill = (dto.getId() != null) 
                ? billingRepository.findById(dto.getId()).orElse(new Billing())
                : new Billing();
        bill.setPatient(patientRepository.findById(dto.getPatientId()).orElseThrow());
        bill.setAmount(BigDecimal.valueOf(dto.getAmount()));
        bill.setBillingDate(Date.valueOf(dto.getBillingDate()));
        bill.setDescription(dto.getDescription());
        bill.setStatus(dto.getStatus());
        return billingRepository.save(bill);
    }

    @Override
    public void updateStatus(Long billId, String status) {
        Billing bill = billingRepository.findById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found"));
        bill.setStatus(status);
        billingRepository.save(bill);
    }

    @Override
    public void deleteById(Long id) {
        billingRepository.deleteById(id);
    }

    @Override
    public List<Billing> findAllWithPatient() {
        return billingRepository.findAllWithPatient();
    }

    @Override
    public List<Billing> findByPatientId(Long patientId) {
        return billingRepository.findByPatientIdOrderByBillingDateDesc(patientId);
    }

    @Override
    public BigDecimal getTotalPaid() {
        return billingRepository.getTotalPaid();
    }

    @Override
    public long countPending() {
        return billingRepository.countPending();
    }

    @Override
    public long countPendingByPatientId(Long patientId) {
        return billingRepository.countPendingByPatientId(patientId);
    }

    @Override
    public BigDecimal getTotalPendingByPatientId(Long patientId) {
        return billingRepository.getTotalPendingByPatientId(patientId);
    }

    @Override
    public BigDecimal getTotalPaidByPatientId(Long patientId) {
        return billingRepository.getTotalPaidByPatientId(patientId);
    }
}