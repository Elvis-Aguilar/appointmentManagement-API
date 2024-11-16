package com.appointment.management.persistance.repository;

import com.appointment.management.persistance.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByCui(String cui);

    boolean existsByNit(String nit);

    boolean existsByPhone(String phone);

    List<UserEntity> findAllByRoleId(Long roleId);

    @Query("SELECT u FROM UserEntity u WHERE u.role.id NOT IN (:excludedRoleIds)")
    List<UserEntity> findAllByRoleIdNotIn(@Param("excludedRoleIds") List<Long> excludedRoleIds);

    long countByRoleName(String roleName);
}
