package com.smartclinic.appointment_system.controller;

import com.smartclinic.appointment_system.dto.AppointmentResponse;
import com.smartclinic.appointment_system.dto.BookAppointmentRequest;
import com.smartclinic.appointment_system.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/book")
    public ResponseEntity<AppointmentResponse> bookAppointment(
            @Valid @RequestBody BookAppointmentRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(appointmentService.bookAppointment(email, request));
    }

    @GetMapping("/my-appointments/patient")
    public ResponseEntity<List<AppointmentResponse>> getMyAppointmentsAsPatient(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                appointmentService.getAppointmentsForPatient(authentication.getName())
        );
    }

    @GetMapping("/my-appointments/doctor")
    public ResponseEntity<List<AppointmentResponse>> getMyAppointmentsAsDoctor(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                appointmentService.getAppointmentsForDoctor(authentication.getName())
        );
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponse> cancelAppointment(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                appointmentService.cancelAppointment(id, authentication.getName())
        );
    }
}