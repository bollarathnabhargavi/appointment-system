package com.smartclinic.appointment_system.service;

import com.smartclinic.appointment_system.dto.AuthResponse;
import com.smartclinic.appointment_system.dto.LoginRequest;
import com.smartclinic.appointment_system.dto.RegisterRequest;
import com.smartclinic.appointment_system.entity.Doctor;
import com.smartclinic.appointment_system.entity.Patient;
import com.smartclinic.appointment_system.entity.User;
import com.smartclinic.appointment_system.repository.DoctorRepository;
import com.smartclinic.appointment_system.repository.PatientRepository;
import com.smartclinic.appointment_system.repository.UserRepository;
import com.smartclinic.appointment_system.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        User savedUser = userRepository.save(user);

        // Create the linked Doctor or Patient profile based on role
        if (savedUser.getRole() == User.Role.DOCTOR) {
            Doctor doctor = new Doctor();
            doctor.setUser(savedUser);
            doctor.setSpecialization("General Medicine"); // default, can be updated later
            doctor.setQualification("MBBS");
            doctor.setExperienceYears(0);
            doctor.setConsultationFee(0.0);
            doctorRepository.save(doctor);
        } else if (savedUser.getRole() == User.Role.PATIENT) {
            Patient patient = new Patient();
            patient.setUser(savedUser);
            patientRepository.save(patient);
        }

        String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getRole().name());

        return new AuthResponse(token, savedUser.getEmail(), savedUser.getName(), savedUser.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return new AuthResponse(token, user.getEmail(), user.getName(), user.getRole().name());
    }
}