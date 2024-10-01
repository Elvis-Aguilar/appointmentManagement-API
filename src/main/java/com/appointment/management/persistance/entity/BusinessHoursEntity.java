package com.appointment.management.persistance.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_permission")
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
public class BusinessHoursEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "business_id", nullable = false)
    private Long businessId;

    //TODO: variables contienen tiempo y fecha, enum de dias de la semana

    @NonNull
    @Column(name = "available_workers", nullable = false)
    private Integer availableWorkers;

    @NonNull
    @Column(name = "available_areas", nullable = false)
    private Integer availableAreas;

    //TODO: maped to realtion




}
