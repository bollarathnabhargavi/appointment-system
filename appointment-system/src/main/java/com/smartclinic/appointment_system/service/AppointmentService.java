package com.smartclinic.appointment_system.service;

import com.smartclinic.appointment_system.dto.AppointmentResponse;
import com.smartclinic.appointment_system.dto.BookAppointmentRequest;
import com.smartclinic.appointment_system.entity.Appointment;
import com.smartclinic.appointment_system.entity.Doctor;
import com.smartclinic.appointment_system.entity.Patient;
import com.smartclinic.appointment_system.exception.SlotUnavailableException;
import com.smartclinic.appointment_system.repository.AppointmentRepository;
import com.smartclinic.appointment_system.repository.DoctorRepository;
import com.smartclinic.appointment_system.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Transactional
    public AppointmentResponse bookAppointment(String patientEmail, BookAppointmentRequest request) {

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        Patient patient = patientRepository.findByUser_Id(
                getUserIdByEmail(patientEmail)
        ).orElseThrow(() -> new IllegalArgumentException("Patient profile not found"));

        // First line of defense: check if the slot is already booked
        boolean slotTaken = appointmentRepository.existsByDoctor_IdAndAppointmentDateTime(
                doctor.getId(), request.getAppointmentDateTime()
        );

        if (slotTaken) {
            throw new SlotUnavailableException(
                    "This slot is already booked. Please choose a different time."
            );
        }

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentDateTime(request.getAppointmentDateTime());
        appointment.setReasonForVisit(request.getReasonForVisit());
        appointment.setStatus(Appointment.Status.BOOKED);

        try {
            // Even with the check above, two requests could race between
            // the check and this save. @Version on Appointment + this
            // try/catch is the second line of defense — Hibernate throws
            // OptimisticLockingFailureException if a concurrent write
            // conflict is detected at the database level.
            Appointment saved = appointmentRepository.save(appointment);
            return mapToResponse(saved);
        } catch (OptimisticLockingFailureException e) {
            throw new SlotUnavailableException(
                    "This slot was just booked by someone else. Please choose a different time."
            );
        }
    }

    public List<AppointmentResponse> getAppointmentsForPatient(String patientEmail) {
        Patient patient = patientRepository.findByUser_Id(getUserIdByEmail(patientEmail))
                .orElseThrow(() -> new IllegalArgumentException("Patient profile not found"));

        return appointmentRepository.findByPatient_Id(patient.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<AppointmentResponse> getAppointmentsForDoctor(String doctorEmail) {
        Doctor doctor = doctorRepository.findByUser_Id(getUserIdByEmail(doctorEmail))
                .orElseThrow(() -> new IllegalArgumentException("Doctor profile not found"));

        return appointmentRepository.findByDoctor_Id(doctor.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AppointmentResponse cancelAppointment(Long appointmentId, String requesterEmail) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        appointment.setStatus(Appointment.Status.CANCELLED);
        Appointment saved = appointmentRepository.save(appointment);
        return mapToResponse(saved);
    }

    private Long getUserIdByEmail(String email) {
        return patientRepository.findAll().stream()
                .filter(p -> p.getUser().getEmail().equals(email))
                .map(p -> p.getUser().getId())
                .findFirst()
                .orElseGet(() -> doctorRepository.findAll().stream()
                        .filter(d -> d.getUser().getEmail().equals(email))
                        .map(d -> d.getUser().getId())
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("User not found")));
    }

    private AppointmentResponse mapToResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getDoctor().getUser().getName(),
                appointment.getDoctor().getSpecialization(),
                appointment.getPatient().getUser().getName(),
                appointment.getAppointmentDateTime(),
                appointment.getStatus().name(),
                appointment.getReasonForVisit()
        );
    }
}