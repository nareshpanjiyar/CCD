package com.hospital.controller;

import com.hospital.dto.AppointmentBookingDto;
import com.hospital.entity.Appointment;
import com.hospital.entity.Billing;
import com.hospital.entity.Patient;
import com.hospital.entity.Prescription;
import com.hospital.entity.PrescriptionItem;
import com.hospital.service.AppointmentService;
import com.hospital.service.BillingService;
import com.hospital.service.DoctorService;
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
import java.util.List;

@Controller
@RequestMapping("/patient")
@PreAuthorize("hasRole('PATIENT')")
public class PatientController {

    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final PrescriptionService prescriptionService;
    private final BillingService billingService;
    private final DoctorService doctorService;

    public PatientController(PatientService patientService,
                             AppointmentService appointmentService,
                             PrescriptionService prescriptionService,
                             BillingService billingService,
                             DoctorService doctorService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
        this.prescriptionService = prescriptionService;
        this.billingService = billingService;
        this.doctorService = doctorService;
    }

    /**
     * Helper to get current patient's ID from Principal.
     */
    private Long getCurrentPatientId(Principal principal) {
        String username = principal.getName();
        Patient patient = patientService.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Patient not found"));
        return patient.getId();
    }

    // ---------- DASHBOARD ----------
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        Long patientId = getCurrentPatientId(principal);
        model.addAttribute("totalAppointments", appointmentService.countByPatientId(patientId));
        model.addAttribute("upcomingAppointments", appointmentService.countUpcomingByPatientId(patientId));
        model.addAttribute("totalPrescriptions", prescriptionService.countByPatientId(patientId));
        model.addAttribute("pendingBills", billingService.countPendingByPatientId(patientId));
        model.addAttribute("pendingAmount", billingService.getTotalPendingByPatientId(patientId));
        model.addAttribute("recentAppointments", appointmentService.findRecentByPatientId(patientId, 5));
        return "patient/dashboard";
    }

    // ---------- APPOINTMENTS ----------
    @GetMapping("/appointments")
    public String listAppointments(Model model, Principal principal) {
        Long patientId = getCurrentPatientId(principal);
        List<Appointment> appointments = appointmentService.findByPatientIdWithDoctor(patientId);
        model.addAttribute("appointments", appointments);
        model.addAttribute("doctors", doctorService.findAll());
        model.addAttribute("appointmentDto", new AppointmentBookingDto());
        return "patient/appointments";
    }

    @PostMapping("/appointments/book")
    public String bookAppointment(@Valid @ModelAttribute("appointmentDto") AppointmentBookingDto dto,
                                  BindingResult result,
                                  Principal principal,
                                  RedirectAttributes redirectAttributes) {
        Long patientId = getCurrentPatientId(principal);
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please fill all required fields correctly.");
            return "redirect:/patient/appointments";
        }
        try {
            appointmentService.bookForPatient(dto, patientId);
            redirectAttributes.addFlashAttribute("message", "Appointment booked successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/patient/appointments";
    }

    @GetMapping("/appointments/cancel/{id}")
    public String cancelAppointment(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        Long patientId = getCurrentPatientId(principal);
        try {
            appointmentService.cancelByIdAndPatientId(id, patientId);
            redirectAttributes.addFlashAttribute("message", "Appointment cancelled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/patient/appointments";
    }

    // ---------- PRESCRIPTIONS ----------
    @GetMapping("/prescriptions")
    public String listPrescriptions(Model model, Principal principal) {
        Long patientId = getCurrentPatientId(principal);
        List<Prescription> prescriptions = prescriptionService.findByPatientIdWithDoctor(patientId);
        model.addAttribute("prescriptions", prescriptions);
        return "patient/prescriptions";
    }

    @GetMapping("/prescriptions/view/{id}")
    public String viewPrescription(@PathVariable Long id, Model model, Principal principal) {
        Long patientId = getCurrentPatientId(principal);
        Prescription prescription = prescriptionService.findByIdWithItems(id);
        // Security check
        if (prescription.getPatient() == null || !prescription.getPatient().getId().equals(patientId)) {
            throw new SecurityException("Access denied");
        }
        model.addAttribute("prescription", prescription);
        return "patient/prescription-detail";
    }

    @GetMapping("/prescriptions/{id}/items")
    @ResponseBody
    public List<PrescriptionItem> getPrescriptionItems(@PathVariable Long id, Principal principal) {
        Long patientId = getCurrentPatientId(principal);
        Prescription prescription = prescriptionService.findByIdWithItems(id);
        if (prescription.getPatient() == null || !prescription.getPatient().getId().equals(patientId)) {
            throw new SecurityException("Access denied");
        }
        return prescription.getItems();
    }

    // ---------- BILLING ----------
    @GetMapping("/billing")
    public String listBills(Model model, Principal principal) {
        Long patientId = getCurrentPatientId(principal);
        List<Billing> bills = billingService.findByPatientId(patientId);
        model.addAttribute("bills", bills);
        model.addAttribute("totalPending", billingService.getTotalPendingByPatientId(patientId));
        model.addAttribute("totalPaid", billingService.getTotalPaidByPatientId(patientId));
        return "patient/billing";
    }
}