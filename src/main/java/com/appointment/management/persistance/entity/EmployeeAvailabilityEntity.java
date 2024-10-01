package com.appointment.management.persistance.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_permission")
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
public class EmployeeAvailabilityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    //TODO: mepd to enum day of week

    //TODO: variales de timepo y fecha

    //realtion of employye
}
