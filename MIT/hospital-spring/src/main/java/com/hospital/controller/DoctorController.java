package com.hospital.controller;

import com.hospital.dto.PrescriptionDto;
import com.hospital.dto.PrescriptionItemDto;
import com.hospital.entity.Appointment;
import com.hospital.entity.Doctor;
import com.hospital.entity.Patient;
import com.hospital.entity.Prescription;
import com.hospital.entity.PrescriptionItem;
import com.hospital.service.AppointmentService;
import com.hospital.service.DoctorService;
import com.hospital.service.DrugService;
import com.hospital.service.PatientService;
import com.hospital.service.PrescriptionService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/doctor")
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorController {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final PatientService patientService;
    private final PrescriptionService prescriptionService;
    private final DrugService drugService;

    public DoctorController(DoctorService doctorService,
                            AppointmentService appointmentService,
                            PatientService patientService,
                            PrescriptionService prescriptionService,
                            DrugService drugService) {
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
        this.patientService = patientService;
        this.prescriptionService = prescriptionService;
        this.drugService = drugService;
    }

    /**
     * Helper to get current doctor's ID from Principal.
     */
    private Long getCurrentDoctorId(Principal principal) {
        String username = principal.getName();
        Doctor doctor = doctorService.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Doctor not found"));
        return doctor.getId();
    }

    // ---------- DASHBOARD ----------
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        Long doctorId = getCurrentDoctorId(principal);
        model.addAttribute("totalAppointments", appointmentService.countByDoctorId(doctorId));
        model.addAttribute("upcomingAppointments", appointmentService.countUpcomingByDoctorId(doctorId));
        model.addAttribute("totalPrescriptions", prescriptionService.countByDoctorId(doctorId));
        model.addAttribute("completedAppointments", appointmentService.countCompletedByDoctorId(doctorId));
        model.addAttribute("recentAppointments", appointmentService.findRecentByDoctorId(doctorId, 5));
        return "doctor/dashboard";
    }

    // ---------- APPOINTMENTS ----------
    @GetMapping("/appointments")
    public String listAppointments(Model model, Principal principal) {
        Long doctorId = getCurrentDoctorId(principal);
        List<Appointment> appointments = appointmentService.findByDoctorIdWithPatient(doctorId);
        model.addAttribute("appointments", appointments);
        return "doctor/appointments";
    }

    @PostMapping("/appointments/update-status")
    public String updateAppointmentStatus(@RequestParam Long appointmentId,
                                          @RequestParam String status,
                                          Principal principal,
                                          RedirectAttributes redirectAttributes) {
        Long doctorId = getCurrentDoctorId(principal);
        try {
            appointmentService.updateStatus(appointmentId, status, doctorId);
            redirectAttributes.addFlashAttribute("message", "Appointment status updated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/doctor/appointments";
    }

    // ---------- PRESCRIPTIONS ----------
    @GetMapping("/prescriptions")
    public String listPrescriptions(Model model, Principal principal) {
        Long doctorId = getCurrentDoctorId(principal);
        List<Prescription> prescriptions = prescriptionService.findByDoctorIdWithPatient(doctorId);
        model.addAttribute("prescriptions", prescriptions);
        return "doctor/prescriptions";
    }

    @GetMapping("/prescriptions/new")
    public String newPrescriptionForm(Model model, Principal principal,
                                      @RequestParam(required = false) Long patientId,
                                      @RequestParam(required = false) Long appointmentId) {
        PrescriptionDto dto = new PrescriptionDto();
        if (patientId != null) {
            dto.setPatientId(patientId);
        }
        if (appointmentId != null) {
            dto.setAppointmentId(appointmentId);
        }
        // Initialize with one empty medication row
        List<PrescriptionItemDto> items = new ArrayList<>();
        items.add(new PrescriptionItemDto());
        dto.setItems(items);

        model.addAttribute("prescriptionDto", dto);
        model.addAttribute("patients", patientService.findAll());
        model.addAttribute("drugs", drugService.findAllInStock());
        model.addAttribute("appointments", appointmentService.findScheduledByDoctorId(getCurrentDoctorId(principal)));
        return "doctor/prescription-form";
    }

    @PostMapping("/prescriptions/save")
    public String savePrescription(@Valid @ModelAttribute("prescriptionDto") PrescriptionDto dto,
                                   BindingResult result,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        Long doctorId = getCurrentDoctorId(principal);
        if (result.hasErrors()) {
            model.addAttribute("patients", patientService.findAll());
            model.addAttribute("drugs", drugService.findAllInStock());
            model.addAttribute("appointments", appointmentService.findScheduledByDoctorId(doctorId));
            return "doctor/prescription-form";
        }
        try {
            prescriptionService.createPrescription(dto, doctorId);
            redirectAttributes.addFlashAttribute("message", "Prescription created successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/doctor/prescriptions";
    }

    @GetMapping("/prescriptions/view/{id}")
    public String viewPrescription(@PathVariable Long id, Model model, Principal principal) {
        Long doctorId = getCurrentDoctorId(principal);
        Prescription prescription = prescriptionService.findByIdWithItems(id);
        // Security: ensure prescription belongs to this doctor
        if (prescription.getDoctor() == null || !prescription.getDoctor().getId().equals(doctorId)) {
            throw new SecurityException("Access denied");
        }
        model.addAttribute("prescription", prescription);
        return "doctor/prescription-detail";
    }

    @GetMapping("/prescriptions/{id}/items")
    @ResponseBody
    public List<PrescriptionItem> getPrescriptionItems(@PathVariable Long id, Principal principal) {
        Long doctorId = getCurrentDoctorId(principal);
        Prescription prescription = prescriptionService.findByIdWithItems(id);
        if (prescription.getDoctor() == null || !prescription.getDoctor().getId().equals(doctorId)) {
            throw new SecurityException("Access denied");
        }
        return prescription.getItems();
    }

    // ---------- PATIENT HISTORY ----------
    @GetMapping("/patient-history")
    public String patientHistory(@RequestParam(required = false) Long patientId, Model model, Principal principal) {
        model.addAttribute("patients", patientService.findAll());
        if (patientId != null) {
            Patient patient = patientService.findById(patientId)
                    .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
            model.addAttribute("selectedPatient", patient);
            model.addAttribute("appointments", appointmentService.findByPatientId(patientId));
            model.addAttribute("prescriptions", prescriptionService.findByPatientId(patientId));
        }
        return "doctor/patient-history";
    }
}