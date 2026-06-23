package com.smartclinic.appointment_system.repository;

import com.smartclinic.appointment_system.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatient_Id(Long patientId);

    List<Appointment> findByDoctor_Id(Long doctorId);

    // Used to check for an existing booking at the exact same slot —
    // the first line of defense against double-booking
    boolean existsByDoctor_IdAndAppointmentDateTime(Long doctorId, LocalDateTime dateTime);

    List<Appointment> findByDoctor_IdAndAppointmentDateTimeBetween(
            Long doctorId, LocalDateTime start, LocalDateTime end);
}