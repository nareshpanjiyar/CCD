package com.hospital.repository;

import com.hospital.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    // For Doctor
    @Query("SELECT p FROM Prescription p JOIN FETCH p.patient WHERE p.doctor.id = :doctorId ORDER BY p.prescriptionDate DESC")
    List<Prescription> findByDoctorIdWithPatient(@Param("doctorId") Long doctorId);
    
    @Query("SELECT COUNT(p) FROM Prescription p WHERE p.doctor.id = :doctorId")
    long countByDoctorId(@Param("doctorId") Long doctorId);

    // For Patient
    @Query("SELECT p FROM Prescription p JOIN FETCH p.doctor WHERE p.patient.id = :patientId ORDER BY p.prescriptionDate DESC")
    List<Prescription> findByPatientIdWithDoctor(@Param("patientId") Long patientId);
    
    @Query("SELECT COUNT(p) FROM Prescription p WHERE p.patient.id = :patientId")
    long countByPatientId(@Param("patientId") Long patientId);

    // Detailed fetch with items
    @Query("SELECT DISTINCT p FROM Prescription p LEFT JOIN FETCH p.items WHERE p.id = :id")
    Optional<Prescription> findByIdWithItems(@Param("id") Long id);

    // Admin view
    @Query("SELECT p FROM Prescription p JOIN FETCH p.patient JOIN FETCH p.doctor ORDER BY p.prescriptionDate DESC")
    List<Prescription> findAllWithDetails();
}