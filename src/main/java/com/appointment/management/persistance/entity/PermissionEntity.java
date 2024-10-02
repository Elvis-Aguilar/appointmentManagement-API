package com.appointment.management.persistance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "permission")
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
public class PermissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false,  length = 50)
    private String name;

    @NonNull
    @Column(nullable = false)
    private String description;

    //manejo de relaciones con tablas hijas
    @OneToMany(mappedBy = "permission", fetch = FetchType.LAZY)
    private List<UserPermissionEntity> userPermissions;

}
