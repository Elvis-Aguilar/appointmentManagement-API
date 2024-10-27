package com.appointment.management.domain.service.appointmet;

import static org.junit.jupiter.api.Assertions.*;

import com.appointment.management.domain.dto.appoinment.CancellationSurchargeDto;
import com.appointment.management.persistance.entity.AppointmentEntity;
import com.appointment.management.persistance.entity.CancellationSurchargeEntity;
import com.appointment.management.persistance.entity.UserEntity;
import com.appointment.management.persistance.enums.StatusCancellation;
import com.appointment.management.persistance.repository.CancellationSurchargeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancellationSurchargeServiceTest {

    @InjectMocks
    private CancellationSurchargeService cancellationSurchargeService;

    @Mock
    private CancellationSurchargeRepository cancellationSurchargeRepository;

    private CancellationSurchargeEntity cancellationSurchargeEntity;

    @BeforeEach
    void setUp() {
        cancellationSurchargeEntity = new CancellationSurchargeEntity();
        cancellationSurchargeEntity.setId(1L);
        cancellationSurchargeEntity.setAppointment(new AppointmentEntity());
        cancellationSurchargeEntity.setCustomer(new UserEntity());
        cancellationSurchargeEntity.setDate(LocalDateTime.now());
        cancellationSurchargeEntity.setStatus(StatusCancellation.PENDING);
    }

    @Test
    void testCreate() {
        // Given
        AppointmentEntity appointmentEntity = new AppointmentEntity();
        appointmentEntity.setId(1L); // Asegúrate de establecer un ID válido

        UserEntity customerEntity = new UserEntity();
        customerEntity.setId(2L); // Asegúrate de establecer un ID válido

        cancellationSurchargeEntity.setAppointment(appointmentEntity);
        cancellationSurchargeEntity.setCustomer(customerEntity);

        when(cancellationSurchargeRepository.save(any(CancellationSurchargeEntity.class)))
                .thenReturn(cancellationSurchargeEntity);

        // When
        CancellationSurchargeDto result = cancellationSurchargeService.create(cancellationSurchargeEntity);

        // Then
        assertNotNull(result);
        assertEquals(cancellationSurchargeEntity.getId(), result.id());
        assertEquals(cancellationSurchargeEntity.getAppointment().getId(), result.appointment());
        assertEquals(cancellationSurchargeEntity.getCustomer().getId(), result.customer());
        assertEquals(cancellationSurchargeEntity.getStatus(), result.status());

        verify(cancellationSurchargeRepository).save(cancellationSurchargeEntity);
    }


    @Test
    void testGetAll() {
        // Given
        AppointmentEntity appointmentEntity = new AppointmentEntity();
        appointmentEntity.setId(1L); // Establece un ID válido

        UserEntity userEntity = new UserEntity();
        userEntity.setId(2L); // Establece un ID válido

        cancellationSurchargeEntity.setId(1L);
        cancellationSurchargeEntity.setAppointment(appointmentEntity);
        cancellationSurchargeEntity.setCustomer(userEntity);
        cancellationSurchargeEntity.setDate(LocalDateTime.now());
        cancellationSurchargeEntity.setStatus(StatusCancellation.PENDING);

        CancellationSurchargeEntity anotherEntity = new CancellationSurchargeEntity();
        anotherEntity.setId(2L);
        anotherEntity.setAppointment(appointmentEntity); // Asignar el mismo o diferente appointment
        anotherEntity.setCustomer(userEntity); // Asignar el mismo o diferente user
        anotherEntity.setDate(LocalDateTime.now());
        anotherEntity.setStatus(StatusCancellation.PENDING);

        when(cancellationSurchargeRepository.findAll()).thenReturn(List.of(cancellationSurchargeEntity, anotherEntity));

        // When
        List<CancellationSurchargeDto> result = cancellationSurchargeService.getAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(cancellationSurchargeEntity.getId(), result.get(0).id());
        assertEquals(anotherEntity.getId(), result.get(1).id());

        verify(cancellationSurchargeRepository).findAll();
    }


    @Test
    void testGetAll_EmptyList() {
        // Given
        when(cancellationSurchargeRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<CancellationSurchargeDto> result = cancellationSurchargeService.getAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(cancellationSurchargeRepository).findAll();
    }
}
