package com.hospital.service;

import com.hospital.dto.AppointmentBookingDto;
import com.hospital.dto.AppointmentDto;
import com.hospital.entity.Appointment;
import java.util.List;

public interface AppointmentService {
    Appointment bookPublicAppointment(AppointmentBookingDto dto);
    Appointment bookForPatient(AppointmentBookingDto dto, Long patientId);
    Appointment bookForAnyPatient(AppointmentBookingDto dto);
    Appointment save(AppointmentDto dto);
    void updateStatus(Long appointmentId, String status);
    void updateStatus(Long appointmentId, String status, Long doctorId);
    void cancelByIdAndPatientId(Long id, Long patientId);
    void deleteById(Long id);
    List<Appointment> findAllWithDetails();
    List<Appointment> findByDoctorIdWithPatient(Long doctorId);
    List<Appointment> findByPatientIdWithDoctor(Long patientId);
    List<Appointment> findRecentByDoctorId(Long doctorId, int limit);
    List<Appointment> findRecentByPatientId(Long patientId, int limit);
    List<Appointment> findScheduledByDoctorId(Long doctorId);
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findRecent(int limit);
    long count();
    long countToday();
    long countByDoctorId(Long doctorId);
    long countUpcomingByDoctorId(Long doctorId);
    long countCompletedByDoctorId(Long doctorId);
    long countByPatientId(Long patientId);
    long countUpcomingByPatientId(Long patientId);
}