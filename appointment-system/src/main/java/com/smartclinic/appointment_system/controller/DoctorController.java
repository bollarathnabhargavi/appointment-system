package com.smartclinic.appointment_system.controller;

import com.smartclinic.appointment_system.dto.DoctorResponse;
import com.smartclinic.appointment_system.entity.Doctor;
import com.smartclinic.appointment_system.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorRepository doctorRepository;

    @GetMapping
    public ResponseEntity<List<DoctorResponse>> getAllDoctors() {
        List<DoctorResponse> doctors = doctorRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<List<DoctorResponse>> getBySpecialization(@PathVariable String specialization) {
        List<DoctorResponse> doctors = doctorRepository.findBySpecializationIgnoreCase(specialization)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> getDoctorById(@PathVariable Long id) {
        return doctorRepository.findById(id)
                .map(this::mapToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private DoctorResponse mapToResponse(Doctor doctor) {
        return new DoctorResponse(
                doctor.getId(),
                doctor.getUser().getName(),
                doctor.getUser().getEmail(),
                doctor.getSpecialization(),
                doctor.getQualification(),
                doctor.getExperienceYears(),
                doctor.getBio(),
                doctor.getConsultationFee()
        );
    }
}