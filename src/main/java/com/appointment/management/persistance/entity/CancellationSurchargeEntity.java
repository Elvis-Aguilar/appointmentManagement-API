package com.appointment.management.persistance.entity;


import com.appointment.management.persistance.enums.StatusCancellation;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "cancellation_surcharge")
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
public class CancellationSurchargeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    private AppointmentEntity appointment;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime date;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private UserEntity customer;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCancellation status;

    //manejo de relaciones con tablas hijas
    @OneToOne(mappedBy = "cancellationSurcharge")
    private InvoiceEntity invoice;

}
