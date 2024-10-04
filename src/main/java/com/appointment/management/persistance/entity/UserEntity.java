package com.appointment.management.persistance.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

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
    @Column(nullable = false, length = 15, unique = true)
    private String phone;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private RoleEntity role;

    @Column(name = "image_url")
    private String imageUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    //relaciones con tablas hijas
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserPermissionEntity> userPermissions;

    @OneToOne(mappedBy = "admin")
    private BusinessConfigurationEntity businessConfiguration;

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private List<EmployeeAvailabilityEntity> employeeAvailabilities;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<AppointmentEntity> appointmentsCustomer;

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private List<AppointmentEntity> appointmentsEmployee;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<InvoiceEntity> customersInvoices;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<CancellationSurchargeEntity> cancellationSurcharges;

    @OneToMany(mappedBy = "userInterest", fetch = FetchType.LAZY)
    private List<InterestScheduleEntity> interestSchedules;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<NotificationEntity> customersNotifications;

}
