package com.hospital.service;

import com.hospital.entity.*;
import com.hospital.repository.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepo;
    private final DoctorRepository doctorRepo;
    private final PatientRepository patientRepo;
    private final ReceptionistRepository receptionistRepo;

    public CustomUserDetailsService(AdminRepository ar, DoctorRepository dr, 
                                    PatientRepository pr, ReceptionistRepository rr) {
        this.adminRepo = ar;
        this.doctorRepo = dr;
        this.patientRepo = pr;
        this.receptionistRepo = rr;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try Admin
        Admin admin = adminRepo.findByUsername(username).orElse(null);
        if (admin != null) {
            return new User(admin.getUsername(), admin.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        }
        // Try Doctor
        Doctor doctor = doctorRepo.findByUsername(username).orElse(null);
        if (doctor != null) {
            return new User(doctor.getUsername(), doctor.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_DOCTOR")));
        }
        // Try Patient
        Patient patient = patientRepo.findByUsername(username).orElse(null);
        if (patient != null) {
            return new User(patient.getUsername(), patient.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_PATIENT")));
        }
        // Try Receptionist
        Receptionist receptionist = receptionistRepo.findByUsername(username).orElse(null);
        if (receptionist != null) {
            return new User(receptionist.getUsername(), receptionist.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_RECEPTIONIST")));
        }
        throw new UsernameNotFoundException("User not found");
    }
}