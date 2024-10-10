package com.appointment.management.persistance.entity;

import com.appointment.management.persistance.enums.BusinessType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "business_configuration")
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
public class BusinessConfigurationEntity {

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
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private UserEntity admin;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @NonNull
    @Column(nullable = false)
    private String description;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "bussiness_type", nullable = false)
    private BusinessType businessType;

    @NonNull
    @Column(name = "max_days_cancellation", nullable = false)
    private Integer maxDaysCancellation;

    @NonNull
    @Column(name = "max_hours_cancellation", nullable = false)
    private Integer maxHoursCancellation;

    @NonNull
    @Column(name = "cancellation_surcharge", nullable = false)
    private BigDecimal cancellationSurcharge;

    @NonNull
    @Column(name = "max_days_update", nullable = false)
    private Integer maxDaysUpdate;

    @NonNull
    @Column(name = "max_hours_update", nullable = false)
    private BigDecimal maxHoursUpdate;

    //realciones con tablas hijas
    @OneToMany(mappedBy = "business", fetch = FetchType.LAZY)
    private List<BusinessHoursEntity> businessHours;

}
