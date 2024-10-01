package com.appointment.management.persistance.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_permission")
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserPermissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NonNull
    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

    //mapear relacionde, tabla intermedia de muchos a muchos
}
