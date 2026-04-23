package com.hospital.repository;

import com.hospital.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByUsername(String username);
    Optional<Patient> findByPhone(String phone);
    List<Patient> findAllByOrderByNameAsc();
    boolean existsByUsername(String username);
    boolean existsByPhone(String phone);
    
    @Query("SELECT p FROM Patient p WHERE p.status = :status ORDER BY p.admissionDate DESC")
    List<Patient> findByStatus(@Param("status") String status);
    
    @Query("SELECT COUNT(p) FROM Patient p WHERE p.status = 'Admitted'")
    long countAdmitted();
}