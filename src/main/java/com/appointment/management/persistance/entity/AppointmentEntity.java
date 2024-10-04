package com.appointment.management.persistance.entity;

import com.appointment.management.persistance.enums.StatusAppointment;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "appointment")
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
public class AppointmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private UserEntity customer;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity service;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private UserEntity employee;

    @NonNull
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @NonNull
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAppointment status;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private StatusAppointment paymentMethod;

    //manejo de relaciones con tablas hijas
    @OneToOne(mappedBy = "appointment")
    private InvoiceEntity invoice;

    @OneToMany(mappedBy = "appointment", fetch = FetchType.LAZY)
    private List<CancellationSurchargeEntity> cancellationSurcharges;

    @OneToMany(mappedBy = "appointment", fetch = FetchType.LAZY)
    private List<InterestScheduleEntity> interestSchedules;


}
