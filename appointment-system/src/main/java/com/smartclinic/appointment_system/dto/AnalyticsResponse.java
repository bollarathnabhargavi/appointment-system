package com.smartclinic.appointment_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalyticsResponse {

    private long totalAppointments;
    private long bookedCount;
    private long completedCount;
    private long cancelledCount;
    private double cancellationRate;
    private Map<String, Long> appointmentsBySpecialization;
    private Map<String, Long> appointmentsByDate;
}