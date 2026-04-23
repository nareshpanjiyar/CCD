package com.hospital.service;

import com.hospital.dto.BillingDto;
import com.hospital.entity.Billing;
import java.math.BigDecimal;
import java.util.List;

public interface BillingService {
    Billing generateBill(BillingDto dto);
    Billing save(BillingDto dto);
    void updateStatus(Long billId, String status);
    void deleteById(Long id);
    List<Billing> findAllWithPatient();
    List<Billing> findByPatientId(Long patientId);
    BigDecimal getTotalPaid();
    long countPending();
    long countPendingByPatientId(Long patientId);
    BigDecimal getTotalPendingByPatientId(Long patientId);
    BigDecimal getTotalPaidByPatientId(Long patientId);
}