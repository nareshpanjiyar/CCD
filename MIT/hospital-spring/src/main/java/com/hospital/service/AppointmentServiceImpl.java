package com.hospital.service.impl;

import com.hospital.dto.AppointmentBookingDto;
import com.hospital.dto.AppointmentDto;
import com.hospital.entity.*;
import com.hospital.repository.*;
import com.hospital.service.AppointmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                  PatientRepository patientRepository,
                                  DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    @Override
    public Appointment bookPublicAppointment(AppointmentBookingDto dto) {
        // Find or create patient by phone
        Patient patient = patientRepository.findByPhone(dto.getPhone())
                .orElseGet(() -> {
                    Patient newPatient = new Patient();
                    newPatient.setUsername(dto.getPhone());
                    newPatient.setPassword(dto.getPhone());
                    newPatient.setName(dto.getName());
                    newPatient.setAge(dto.getAge());
                    newPatient.setGender(dto.getGender());
                    newPatient.setPhone(dto.getPhone());
                    newPatient.setEmail(dto.getEmail());
                    return patientRepository.save(newPatient);
                });
        return createAppointment(patient.getId(), dto.getDoctorId(), dto.getAppointmentDate(), dto.getReason());
    }

    @Override
    public Appointment bookForPatient(AppointmentBookingDto dto, Long patientId) {
        return createAppointment(patientId, dto.getDoctorId(), dto.getAppointmentDate(), dto.getReason());
    }

    @Override
    public Appointment bookForAnyPatient(AppointmentBookingDto dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        return createAppointment(patient.getId(), dto.getDoctorId(), dto.getAppointmentDate(), dto.getReason());
    }

    private Appointment createAppointment(Long patientId, Long doctorId, java.time.LocalDateTime dateTime, String reason) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(Timestamp.valueOf(dateTime));
        appointment.setReason(reason);
        appointment.setStatus("Scheduled");
        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment save(AppointmentDto dto) {
        Appointment appointment = (dto.getId() != null) 
                ? appointmentRepository.findById(dto.getId()).orElse(new Appointment())
                : new Appointment();
        appointment.setPatient(patientRepository.findById(dto.getPatientId()).orElseThrow());
        appointment.setDoctor(doctorRepository.findById(dto.getDoctorId()).orElseThrow());
        appointment.setAppointmentDate(Timestamp.valueOf(dto.getAppointmentDate()));
        appointment.setReason(dto.getReason());
        appointment.setStatus(dto.getStatus());
        return appointmentRepository.save(appointment);
    }

    @Override
    public void updateStatus(Long appointmentId, String status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        appointment.setStatus(status);
        appointmentRepository.save(appointment);
    }

    @Override
    public void updateStatus(Long appointmentId, String status, Long doctorId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        if (!appointment.getDoctor().getId().equals(doctorId)) {
            throw new SecurityException("Access denied");
        }
        appointment.setStatus(status);
        appointmentRepository.save(appointment);
    }

    @Override
    public void cancelByIdAndPatientId(Long id, Long patientId) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        if (!appointment.getPatient().getId().equals(patientId)) {
            throw new SecurityException("Access denied");
        }
        appointment.setStatus("Cancelled");
        appointmentRepository.save(appointment);
    }

    @Override
    public void deleteById(Long id) {
        appointmentRepository.deleteById(id);
    }

    @Override
    public List<Appointment> findAllWithDetails() {
        return appointmentRepository.findAllWithDetails();
    }

    @Override
    public List<Appointment> findByDoctorIdWithPatient(Long doctorId) {
        return appointmentRepository.findByDoctorIdOrderByAppointmentDateDesc(doctorId);
    }

    @Override
    public List<Appointment> findByPatientIdWithDoctor(Long patientId) {
        return appointmentRepository.findByPatientIdOrderByAppointmentDateDesc(patientId);
    }

    @Override
    public List<Appointment> findRecentByDoctorId(Long doctorId, int limit) {
        List<Appointment> list = appointmentRepository.findByDoctorIdOrderByAppointmentDateDesc(doctorId);
        return list.stream().limit(limit).toList();
    }

    @Override
    public List<Appointment> findRecentByPatientId(Long patientId, int limit) {
        List<Appointment> list = appointmentRepository.findByPatientIdOrderByAppointmentDateDesc(patientId);
        return list.stream().limit(limit).toList();
    }

    @Override
    public List<Appointment> findScheduledByDoctorId(Long doctorId) {
        return appointmentRepository.findScheduledByDoctorIdWithPatient(doctorId);
    }

    @Override
    public List<Appointment> findByPatientId(Long patientId) {
        return appointmentRepository.findByPatientIdOrderByAppointmentDateDesc(patientId);
    }

    @Override
    public List<Appointment> findRecent(int limit) {
        return appointmentRepository.findTop5ByOrderByAppointmentDateDesc();
    }

    @Override
    public long count() {
        return appointmentRepository.count();
    }

    @Override
    public long countToday() {
        return appointmentRepository.countToday();
    }

    @Override
    public long countByDoctorId(Long doctorId) {
        return appointmentRepository.countByDoctorId(doctorId);
    }

    @Override
    public long countUpcomingByDoctorId(Long doctorId) {
        return appointmentRepository.countUpcomingByDoctorId(doctorId);
    }

    @Override
    public long countCompletedByDoctorId(Long doctorId) {
        return appointmentRepository.countCompletedByDoctorId(doctorId);
    }

    @Override
    public long countByPatientId(Long patientId) {
        return appointmentRepository.countByPatientId(patientId);
    }

    @Override
    public long countUpcomingByPatientId(Long patientId) {
        return appointmentRepository.countUpcomingByPatientId(patientId);
    }
}