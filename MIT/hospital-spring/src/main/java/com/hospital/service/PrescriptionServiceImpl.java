package com.hospital.service.impl;

import com.hospital.dto.PrescriptionDto;
import com.hospital.dto.PrescriptionItemDto;
import com.hospital.entity.*;
import com.hospital.repository.*;
import com.hospital.service.PrescriptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final DrugRepository drugRepository;

    public PrescriptionServiceImpl(PrescriptionRepository prescriptionRepository,
                                   PatientRepository patientRepository,
                                   DoctorRepository doctorRepository,
                                   AppointmentRepository appointmentRepository,
                                   DrugRepository drugRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.drugRepository = drugRepository;
    }

    @Override
    public Prescription createPrescription(PrescriptionDto dto, Long doctorId) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        
        Prescription prescription = new Prescription();
        prescription.setPatient(patient);
        prescription.setDoctor(doctor);
        if (dto.getAppointmentId() != null) {
            appointmentRepository.findById(dto.getAppointmentId())
                    .ifPresent(prescription::setAppointment);
        }
        prescription.setDiagnosis(dto.getDiagnosis());
        prescription.setNotes(dto.getNotes());
        
        List<PrescriptionItem> items = new ArrayList<>();
        for (PrescriptionItemDto itemDto : dto.getItems()) {
            if (itemDto.getDrugId() == null) continue;
            Drug drug = drugRepository.findById(itemDto.getDrugId())
                    .orElseThrow(() -> new IllegalArgumentException("Drug not found"));
            PrescriptionItem item = new PrescriptionItem();
            item.setPrescription(prescription);
            item.setDrug(drug);
            item.setDosage(itemDto.getDosage());
            item.setFrequency(itemDto.getFrequency());
            item.setDuration(itemDto.getDuration());
            item.setInstructions(itemDto.getInstructions());
            items.add(item);
        }
        prescription.setItems(items);
        return prescriptionRepository.save(prescription);
    }

    @Override
    public Prescription findByIdWithItems(Long id) {
        return prescriptionRepository.findByIdWithItems(id)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found"));
    }

    @Override
    public List<Prescription> findAllWithDetails() {
        return prescriptionRepository.findAllWithDetails();
    }

    @Override
    public List<Prescription> findByDoctorIdWithPatient(Long doctorId) {
        return prescriptionRepository.findByDoctorIdWithPatient(doctorId);
    }

    @Override
    public List<Prescription> findByPatientIdWithDoctor(Long patientId) {
        return prescriptionRepository.findByPatientIdWithDoctor(patientId);
    }

    @Override
    public long countByDoctorId(Long doctorId) {
        return prescriptionRepository.countByDoctorId(doctorId);
    }

    @Override
    public long countByPatientId(Long patientId) {
        return prescriptionRepository.countByPatientId(patientId);
    }

    @Override
    public long count() {
        return prescriptionRepository.count();
    }
}