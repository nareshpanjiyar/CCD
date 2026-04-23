import java.util.Optional;
package com.hospital.repository;

import com.hospital.entity.Appointment;
import com.hospital.entity.Doctor;
import com.hospital.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // For Doctor
    List<Appointment> findByDoctorIdOrderByAppointmentDateDesc(Long doctorId);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId")
    long countByDoctorId(@Param("doctorId") Long doctorId);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId AND a.status = 'Scheduled' AND a.appointmentDate >= CURRENT_TIMESTAMP")
    long countUpcomingByDoctorId(@Param("doctorId") Long doctorId);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId AND a.status = 'Completed'")
    long countCompletedByDoctorId(@Param("doctorId") Long doctorId);
    
    @Query("SELECT a FROM Appointment a JOIN FETCH a.patient WHERE a.doctor.id = :doctorId AND a.status = 'Scheduled' ORDER BY a.appointmentDate")
    List<Appointment> findScheduledByDoctorIdWithPatient(@Param("doctorId") Long doctorId);

    // For Patient
    List<Appointment> findByPatientIdOrderByAppointmentDateDesc(Long patientId);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.patient.id = :patientId")
    long countByPatientId(@Param("patientId") Long patientId);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.patient.id = :patientId AND a.status = 'Scheduled' AND a.appointmentDate >= CURRENT_TIMESTAMP")
    long countUpcomingByPatientId(@Param("patientId") Long patientId);

    // General
    @Query("SELECT a FROM Appointment a JOIN FETCH a.patient JOIN FETCH a.doctor ORDER BY a.appointmentDate DESC")
    List<Appointment> findAllWithDetails();
    
    @Query("SELECT a FROM Appointment a JOIN FETCH a.patient JOIN FETCH a.doctor WHERE a.id = :id")
    Optional<Appointment> findByIdWithDetails(@Param("id") Long id);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE DATE(a.appointmentDate) = CURRENT_DATE")
    long countToday();
    
    List<Appointment> findTop5ByOrderByAppointmentDateDesc();
}