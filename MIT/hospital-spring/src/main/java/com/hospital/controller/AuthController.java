package com.hospital.controller;

import com.hospital.dto.AppointmentBookingDto;
import com.hospital.entity.Doctor;
import com.hospital.service.AppointmentService;
import com.hospital.service.DoctorService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AuthController {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;

    public AuthController(DoctorService doctorService, AppointmentService appointmentService) {
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
    }

    /**
     * Public homepage – displays services, doctors, and the login modal.
     */
    @GetMapping("/index")
    public String indexPage(Model model) {
        List<Doctor> doctors = doctorService.findAll();
        model.addAttribute("doctors", doctors);
        model.addAttribute("appointmentDto", new AppointmentBookingDto());
        return "index";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/index";
    }

    /**
     * Processes the public appointment booking form.
     * Creates a new patient account (if phone not registered) and books the appointment.
     */
    @PostMapping("/book-appointment")
    public String bookAppointment(@ModelAttribute AppointmentBookingDto dto,
                                  RedirectAttributes redirectAttributes) {
        try {
            appointmentService.bookPublicAppointment(dto);
            redirectAttributes.addFlashAttribute("bookingMessage",
                    "Appointment booked! Your login: Username = " + dto.getPhone() +
                    ", Password = " + dto.getPhone());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("bookingError", "Booking failed: " + e.getMessage());
        }
        return "redirect:/index#book";
    }

    /**
     * Post‑login redirection endpoint.
     * Redirects the user to the appropriate dashboard based on their role.
     */
    @GetMapping("/dashboard")
    public String dashboardRedirect(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/index";
        }

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            switch (role) {
                case "ROLE_ADMIN":
                    return "redirect:/admin/dashboard";
                case "ROLE_DOCTOR":
                    return "redirect:/doctor/dashboard";
                case "ROLE_PATIENT":
                    return "redirect:/patient/dashboard";
                case "ROLE_RECEPTIONIST":
                    return "redirect:/receptionist/dashboard";
            }
        }
        return "redirect:/index";
    }
}