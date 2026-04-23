package com.hospital.controller;

import com.hospital.dto.BillingDto;
import com.hospital.dto.PatientRegistrationDto;
import com.hospital.entity.Appointment;
import com.hospital.entity.Patient;
import com.hospital.service.AdmissionService;
import com.hospital.service.AppointmentService;
import com.hospital.service.BillingService;
import com.hospital.service.DoctorService;
import com.hospital.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/receptionist")
@PreAuthorize("hasRole('RECEPTIONIST')")
public class ReceptionistController {

    private final PatientService patientService;
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final BillingService billingService;
    private final AdmissionService admissionService;

    public ReceptionistController(PatientService patientService,
                                  DoctorService doctorService,
                                  AppointmentService appointmentService,
                                  BillingService billingService,
                                  AdmissionService admissionService) {
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
        this.billingService = billingService;
        this.admissionService = admissionService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalPatients", patientService.count());
        model.addAttribute("todayAppointments", appointmentService.countToday());
        model.addAttribute("pendingBills", billingService.countPending());
        model.addAttribute("admittedPatients", admissionService.countAdmitted());
        return "receptionist/dashboard";
    }

    // ===================== PATIENT REGISTRATION =====================
    @GetMapping("/register-patient")
    public String showRegistrationForm(Model model) {
        model.addAttribute("patientDto", new PatientRegistrationDto());
        return "receptionist/register-patient";
    }

    @PostMapping("/register-patient")
    public String registerPatient(@Valid @ModelAttribute("patientDto") PatientRegistrationDto dto,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "receptionist/register-patient";
        }
        patientService.registerPatient(dto);
        redirectAttributes.addFlashAttribute("message", "Patient registered successfully.");
        return "redirect:/receptionist/register-patient";
    }

    // ===================== APPOINTMENTS =====================
    @GetMapping("/appointments")
    public String listAppointments(Model model) {
        model.addAttribute("appointments", appointmentService.findAll());
        model.addAttribute("patients", patientService.findAll());
        model.addAttribute("doctors", doctorService.findAll());
        return "receptionist/appointments";
    }

    @PostMapping("/appointments/save")
    public String saveAppointment(@RequestParam Long patientId,
                                  @RequestParam Long doctorId,
                                  @RequestParam String appointmentDate,
                                  @RequestParam(required = false) String reason,
                                  RedirectAttributes redirectAttributes) {
        appointmentService.bookAppointment(patientId, doctorId, appointmentDate, reason);
        redirectAttributes.addFlashAttribute("message", "Appointment booked.");
        return "redirect:/receptionist/appointments";
    }

    @GetMapping("/appointments/delete/{id}")
    public String deleteAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        appointmentService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Appointment deleted.");
        return "redirect:/receptionist/appointments";
    }

    // ===================== BILLING =====================
    @GetMapping("/billing")
    public String listBills(Model model) {
        model.addAttribute("bills", billingService.findAll());
        model.addAttribute("patients", patientService.findAll());
        model.addAttribute("billDto", new BillingDto());
        return "receptionist/billing";
    }

    @PostMapping("/billing/save")
    public String saveBill(@Valid @ModelAttribute("billDto") BillingDto dto,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Validation failed.");
            return "redirect:/receptionist/billing";
        }
        billingService.save(dto);
        redirectAttributes.addFlashAttribute("message", "Bill saved.");
        return "redirect:/receptionist/billing";
    }

    @GetMapping("/billing/delete/{id}")
    public String deleteBill(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        billingService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Bill deleted.");
        return "redirect:/receptionist/billing";
    }

    // ===================== ADMISSIONS / DISCHARGE =====================
    @GetMapping("/admissions")
    public String listAdmissions(Model model) {
        model.addAttribute("patients", patientService.findAll());
        return "receptionist/admissions";
    }

    @PostMapping("/admissions/update")
    public String updateAdmissionStatus(@RequestParam Long patientId,
                                        @RequestParam String status,
                                        @RequestParam(required = false) String admissionDate,
                                        RedirectAttributes redirectAttributes) {
        admissionService.updateStatus(patientId, status, admissionDate);
        redirectAttributes.addFlashAttribute("message", "Status updated.");
        return "redirect:/receptionist/admissions";
    }
}