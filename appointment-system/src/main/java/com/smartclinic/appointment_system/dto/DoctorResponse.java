package com.smartclinic.appointment_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorResponse {

    private Long id;
    private String name;
    private String email;
    private String specialization;
    private String qualification;
    private Integer experienceYears;
    private String bio;
    private Double consultationFee;
}