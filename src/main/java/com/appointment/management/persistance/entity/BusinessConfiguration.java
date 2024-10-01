package com.appointment.management.persistance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "user_permission")
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
public class BusinessConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String name;

    @NonNull
    @Column(name = "logo_url",nullable = false)
    private String logoUrl;

    @NonNull
    @Column(name = "admin_id", nullable = false)
    private Long adminId;

    @NonNull
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @NonNull
    @Column(nullable = false)
    private String description;

    //TODO: maped to enum


    @NonNull
    @Column(name = "max_days_cancellation", nullable = false)
    private Integer maxDaysCancellation;

    @NonNull
    @Column(name = "max_hours_cancellation", nullable = false)
    private Integer maxHoursCancellation;

    @NonNull
    @Column(name = "cancellation_surcharge", nullable = false)
    private Double cancellationSurcharge; //verificacion de tipo de varable para decimales ver maximo de decimales

    @NonNull
    @Column(name = "max_days_update", nullable = false)
    private Integer maxDaysUpdate;

    //TODO: maped to relation





}
