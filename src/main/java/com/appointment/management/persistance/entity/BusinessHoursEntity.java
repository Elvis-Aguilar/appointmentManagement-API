package com.appointment.management.persistance.entity;

import com.appointment.management.persistance.enums.DayOfWeek;
import com.appointment.management.persistance.enums.StatusBusinessHours;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "business_hours")
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
public class BusinessHoursEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private BusinessConfigurationEntity business;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "specific_date")
    private LocalDate specificDate;

    @NonNull
    @Column(name = "opening_time", nullable = false)
    private LocalTime openingTime;

    @NonNull
    @Column(name = "closing_time", nullable = false)
    private LocalTime closingTime;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusBusinessHours status;

    @NonNull
    @Column(name = "available_workers", nullable = false)
    private Integer availableWorkers;

    @NonNull
    @Column(name = "available_areas", nullable = false)
    private Integer availableAreas;

}
