package com.hospital.controller;

import com.hospital.dto.AppointmentDto;
import com.hospital.dto.BillingDto;
import com.hospital.dto.DoctorDto;
import com.hospital.dto.DrugDto;
import com.hospital.dto.PatientDto;
import com.hospital.dto.ReceptionistDto;
import com.hospital.service.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final PatientService patientService;
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final DrugService drugService;
    private final PrescriptionService prescriptionService;
    private final BillingService billingService;
    private final ReceptionistService receptionistService;
    private final AdmissionService admissionService;

    public AdminController(PatientService patientService,
                           DoctorService doctorService,
                           AppointmentService appointmentService,
                           DrugService drugService,
                           PrescriptionService prescriptionService,
                           BillingService billingService,
                           ReceptionistService receptionistService,
                           AdmissionService admissionService) {
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
        this.drugService = drugService;
        this.prescriptionService = prescriptionService;
        this.billingService = billingService;
        this.receptionistService = receptionistService;
        this.admissionService = admissionService;
    }

    // ---------- DASHBOARD ----------
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalPatients", patientService.count());
        model.addAttribute("totalDoctors", doctorService.count());
        model.addAttribute("totalAppointments", appointmentService.count());
        model.addAttribute("totalRevenue", billingService.getTotalPaid());
        model.addAttribute("lowStockCount", drugService.countLowStock());
        model.addAttribute("pendingBills", billingService.countPending());
        model.addAttribute("recentAppointments", appointmentService.findRecent(5));
        return "admin/dashboard";
    }

    // ---------- PATIENTS ----------
    @GetMapping("/patients")
    public String listPatients(Model model) {
        model.addAttribute("patients", patientService.findAll());
        model.addAttribute("patientDto", new PatientDto());
        return "admin/patients";
    }

    @PostMapping("/patients/save")
    public String savePatient(@Valid @ModelAttribute("patientDto") PatientDto dto,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Validation failed");
            return "redirect:/admin/patients";
        }
        try {
            patientService.save(dto);
            redirectAttributes.addFlashAttribute("message", "Patient saved successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/patients";
    }

    @GetMapping("/patients/delete/{id}")
    public String deletePatient(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            patientService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Patient deleted");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/patients";
    }

    // ---------- DOCTORS ----------
    @GetMapping("/doctors")
    public String listDoctors(Model model) {
        model.addAttribute("doctors", doctorService.findAll());
        model.addAttribute("doctorDto", new DoctorDto());
        return "admin/doctors";
    }

    @PostMapping("/doctors/save")
    public String saveDoctor(@Valid @ModelAttribute("doctorDto") DoctorDto dto,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Validation failed");
            return "redirect:/admin/doctors";
        }
        try {
            doctorService.save(dto);
            redirectAttributes.addFlashAttribute("message", "Doctor saved successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/doctors";
    }

    @GetMapping("/doctors/delete/{id}")
    public String deleteDoctor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            doctorService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Doctor deleted");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/doctors";
    }

    // ---------- APPOINTMENTS ----------
    @GetMapping("/appointments")
    public String listAppointments(Model model) {
        model.addAttribute("appointments", appointmentService.findAllWithDetails());
        model.addAttribute("patients", patientService.findAll());
        model.addAttribute("doctors", doctorService.findAll());
        model.addAttribute("appointmentDto", new AppointmentDto());
        return "admin/appointments";
    }

    @PostMapping("/appointments/save")
    public String saveAppointment(@Valid @ModelAttribute("appointmentDto") AppointmentDto dto,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Validation failed");
            return "redirect:/admin/appointments";
        }
        try {
            appointmentService.save(dto);
            redirectAttributes.addFlashAttribute("message", "Appointment saved");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/appointments";
    }

    @GetMapping("/appointments/delete/{id}")
    public String deleteAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Appointment deleted");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/appointments";
    }

    // ---------- DRUGS ----------
    @GetMapping("/drugs")
    public String listDrugs(Model model) {
        model.addAttribute("drugs", drugService.findAll());
        model.addAttribute("drugDto", new DrugDto());
        return "admin/drugs";
    }

    @PostMapping("/drugs/save")
    public String saveDrug(@Valid @ModelAttribute("drugDto") DrugDto dto,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Validation failed");
            return "redirect:/admin/drugs";
        }
        try {
            drugService.save(dto);
            redirectAttributes.addFlashAttribute("message", "Drug saved");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/drugs";
    }

    @GetMapping("/drugs/delete/{id}")
    public String deleteDrug(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            drugService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Drug deleted");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/drugs";
    }

    // ---------- PRESCRIPTIONS ----------
    @GetMapping("/prescriptions")
    public String listPrescriptions(Model model) {
        model.addAttribute("prescriptions", prescriptionService.findAllWithDetails());
        return "admin/prescriptions";
    }

    @GetMapping("/prescriptions/view/{id}")
    public String viewPrescription(@PathVariable Long id, Model model) {
        model.addAttribute("prescription", prescriptionService.findByIdWithItems(id));
        return "admin/prescription-detail";
    }

    // ---------- BILLING ----------
    @GetMapping("/billing")
    public String listBilling(Model model) {
        model.addAttribute("bills", billingService.findAllWithPatient());
        model.addAttribute("patients", patientService.findAll());
        model.addAttribute("billingDto", new BillingDto());
        return "admin/billing";
    }

    @PostMapping("/billing/save")
    public String saveBill(@Valid @ModelAttribute("billingDto") BillingDto dto,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Validation failed");
            return "redirect:/admin/billing";
        }
        try {
            billingService.save(dto);
            redirectAttributes.addFlashAttribute("message", "Bill saved");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/billing";
    }

    @GetMapping("/billing/delete/{id}")
    public String deleteBill(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            billingService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Bill deleted");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/billing";
    }

    // ---------- RECEPTIONISTS ----------
    @GetMapping("/receptionists")
    public String listReceptionists(Model model) {
        model.addAttribute("receptionists", receptionistService.findAll());
        model.addAttribute("receptionistDto", new ReceptionistDto());
        return "admin/receptionists";
    }

    @PostMapping("/receptionists/save")
    public String saveReceptionist(@Valid @ModelAttribute("receptionistDto") ReceptionistDto dto,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Validation failed");
            return "redirect:/admin/receptionists";
        }
        try {
            receptionistService.save(dto);
            redirectAttributes.addFlashAttribute("message", "Receptionist saved");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/receptionists";
    }

    @GetMapping("/receptionists/delete/{id}")
    public String deleteReceptionist(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            receptionistService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Receptionist deleted");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/receptionists";
    }

    // ---------- ADMISSIONS ----------
    @GetMapping("/admissions")
    public String listAdmissions(Model model) {
        model.addAttribute("patients", admissionService.getAllPatientsWithStatus());
        return "admin/admissions";
    }

    @PostMapping("/admissions/update")
    public String updateAdmissionStatus(@RequestParam Long patientId,
                                        @RequestParam String status,
                                        @RequestParam(required = false) String admissionDate,
                                        RedirectAttributes redirectAttributes) {
        try {
            admissionService.updateStatus(patientId, status, admissionDate);
            redirectAttributes.addFlashAttribute("message", "Status updated");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/admissions";
    }
}