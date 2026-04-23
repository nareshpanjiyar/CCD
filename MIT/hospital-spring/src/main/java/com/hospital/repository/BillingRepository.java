package com.hospital.repository;

import com.hospital.entity.Billing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BillingRepository extends JpaRepository<Billing, Long> {

    @Query("SELECT b FROM Billing b JOIN FETCH b.patient ORDER BY b.billingDate DESC")
    List<Billing> findAllWithPatient();

    List<Billing> findByPatientIdOrderByBillingDateDesc(Long patientId);
    
    @Query("SELECT SUM(b.amount) FROM Billing b WHERE b.status = 'Paid'")
    BigDecimal getTotalPaid();
    
    @Query("SELECT SUM(b.amount) FROM Billing b WHERE b.status = 'Pending'")
    BigDecimal getTotalPending();
    
    @Query("SELECT COUNT(b) FROM Billing b WHERE b.status = 'Pending'")
    long countPending();
    
    @Query("SELECT COUNT(b) FROM Billing b WHERE b.patient.id = :patientId AND b.status = 'Pending'")
    long countPendingByPatientId(@Param("patientId") Long patientId);
    
    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM Billing b WHERE b.patient.id = :patientId AND b.status = 'Pending'")
    BigDecimal getTotalPendingByPatientId(@Param("patientId") Long patientId);
    
    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM Billing b WHERE b.patient.id = :patientId AND b.status = 'Paid'")
    BigDecimal getTotalPaidByPatientId(@Param("patientId") Long patientId);
}