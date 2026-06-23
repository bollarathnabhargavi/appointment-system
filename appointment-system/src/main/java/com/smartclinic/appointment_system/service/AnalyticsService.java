package com.smartclinic.appointment_system.service;

import com.smartclinic.appointment_system.dto.AnalyticsResponse;
import com.smartclinic.appointment_system.entity.Appointment;
import com.smartclinic.appointment_system.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AppointmentRepository appointmentRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public AnalyticsResponse getAnalytics() {
        List<Appointment> all = appointmentRepository.findAll();

        long total = all.size();

        long booked = all.stream()
                .filter(a -> a.getStatus() == Appointment.Status.BOOKED)
                .count();

        long completed = all.stream()
                .filter(a -> a.getStatus() == Appointment.Status.COMPLETED)
                .count();

        long cancelled = all.stream()
                .filter(a -> a.getStatus() == Appointment.Status.CANCELLED)
                .count();

        double cancellationRate = total == 0 ? 0.0 : (cancelled * 100.0) / total;

        Map<String, Long> bySpecialization = all.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getDoctor().getSpecialization(),
                        Collectors.counting()
                ));

        Map<String, Long> byDate = all.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getAppointmentDateTime().format(DATE_FORMATTER),
                        Collectors.counting()
                ));

        return new AnalyticsResponse(
                total, booked, completed, cancelled,
                Math.round(cancellationRate * 100) / 100.0,
                bySpecialization,
                byDate
        );
    }
}