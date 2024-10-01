package com.appointment.management.persistance.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification")
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @NonNull
    @Column(nullable = false)
    private String description;

    //TODO: create maped status enum, createdAt, relation customer
}
