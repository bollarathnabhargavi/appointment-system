package com.smartclinic.appointment_system.repository;

import com.smartclinic.appointment_system.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findBySpecializationIgnoreCase(String specialization);

    Optional<Doctor> findByUser_Id(Long userId);
}