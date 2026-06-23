package com.smartclinic.appointment_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String specialization;

    @Column(nullable = false)
    private String qualification;

    @Column(nullable = false)
    private Integer experienceYears;

    @Column(length = 1000)
    private String bio;

    @Column(nullable = false)
    private Double consultationFee;
}