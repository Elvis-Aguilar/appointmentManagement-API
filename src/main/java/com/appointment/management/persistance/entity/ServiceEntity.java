package com.appointment.management.persistance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "service")
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
public class ServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String name;

    @NonNull
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @NonNull
    @Column(nullable = false)
    private LocalTime duration;

    @NonNull
    @Column(nullable = false)
    private String description;

    @Column(name = "people_reaches")
    private Integer peopleReaches;

    private String location;

    @Column(name = "image_url")
    private String imageUrl;

    //manejo de relaciones con tablas hijas
    @OneToMany(mappedBy = "service", fetch = FetchType.LAZY)
    private List<AppointmentEntity> appointments;


}
