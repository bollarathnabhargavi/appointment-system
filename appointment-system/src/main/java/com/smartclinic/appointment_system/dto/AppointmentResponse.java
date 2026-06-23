package com.smartclinic.appointment_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponse {

    private Long id;
    private String doctorName;
    private String doctorSpecialization;
    private String patientName;
    private LocalDateTime appointmentDateTime;
    private String status;
    private String reasonForVisit;
}