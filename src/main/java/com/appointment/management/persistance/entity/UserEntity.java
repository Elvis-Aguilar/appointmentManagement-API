package com.appointment.management.persistance.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String name;

    @NonNull
    @Column(nullable = false,  length = 15, unique = true)
    private String cui;

    @NonNull
    @Column(nullable = false)
    private String password;

    @NonNull
    @Column(nullable = false,  length = 13, unique = true)
    private String nit;

    @NonNull
    @Column(nullable = false, unique = true)
    private String email;

    @NonNull
    @Column(nullable = false, unique = true)
    private String phone;

    @NonNull
    @Column(name = "role_id", nullable = false)
    private Long roleId;

    //columna de date

    @NonNull
    @Column(name = "image_url", nullable = true, unique = true)
    private String imageUrl;

    //mapeo de relaciones de

}
