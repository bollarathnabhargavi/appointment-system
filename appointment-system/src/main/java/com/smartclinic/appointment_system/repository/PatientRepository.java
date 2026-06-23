package com.smartclinic.appointment_system.repository;

import com.smartclinic.appointment_system.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByUser_Id(Long userId);
}