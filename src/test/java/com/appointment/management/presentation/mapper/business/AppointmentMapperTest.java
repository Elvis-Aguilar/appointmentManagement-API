package com.appointment.management.presentation.mapper.business;

import static org.junit.jupiter.api.Assertions.*;


import com.appointment.management.domain.dto.appoinment.AppointmentDto;
import com.appointment.management.persistance.entity.AppointmentEntity;
import com.appointment.management.persistance.entity.RoleEntity;
import com.appointment.management.persistance.entity.ServiceEntity;
import com.appointment.management.persistance.entity.UserEntity;
import com.appointment.management.persistance.enums.PaymentMethod;
import com.appointment.management.persistance.enums.StatusAppointment;
import com.appointment.management.persistance.enums.StatusBusinessHours;
import com.appointment.management.persistance.repository.ServiceRepository;
import com.appointment.management.persistance.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AppointmentMapperTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private AppointmentMapper appointmentMapper;

    private AppointmentDto appointmentDto;
    private AppointmentEntity appointmentEntity;
    private UserEntity customer;
    private UserEntity employee;
    private ServiceEntity service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        //Given Global
        customer = new UserEntity();
        customer.setId(1L);
        employee = new UserEntity();
        employee.setId(2L);
        service = new ServiceEntity("Service", BigDecimal.valueOf(100), LocalTime.of(1, 0), "Description");

        //Given
        appointmentDto = new AppointmentDto(
                1L,
                customer.getId(),
                service.getId(),
                employee.getId(),
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                "RESERVED",
                "CARD", false
        );

        //Given
        appointmentEntity = new AppointmentEntity(
                customer,
                service,
                employee,
                appointmentDto.startDate(),
                appointmentDto.endDate(),
                StatusAppointment.RESERVED,
                PaymentMethod.CARD
        );
    }

    @Test
    void toEntity_ShouldThrowException_WhenCustomerIsNotFound() {
        //When
        when(userRepository.findById(customer.getId())).thenReturn(Optional.empty());
        when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
        when(userRepository.findById(employee.getId())).thenReturn(Optional.of(employee));

        //Then
        assertThrows(AssertionError.class, () -> appointmentMapper.toEntity(appointmentDto));
    }

    @Test
    void toEntity_ShouldThrowException_WhenServiceIsNotFound() {
        // When
        when(userRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(serviceRepository.findById(service.getId())).thenReturn(Optional.empty());
        when(userRepository.findById(employee.getId())).thenReturn(Optional.of(employee));

        // Then
        assertThrows(AssertionError.class, () -> appointmentMapper.toEntity(appointmentDto));
    }

    @Test
    void toDto_ShouldReturnDto_WhenEntityIsValid() {
        //Given
        AppointmentDto result = appointmentMapper.toDto(appointmentEntity);

        //Then
        assertNotNull(result);
        assertEquals(appointmentEntity.getId(), result.id());
        assertEquals(appointmentEntity.getStartDate(), result.startDate());
        assertEquals(appointmentEntity.getEndDate(), result.endDate());
        assertEquals(appointmentEntity.getStatus().name(), result.status());
        assertEquals(appointmentEntity.getPaymentMethod().name(), result.paymentMethod());
    }

    @Test
    void updateEntityFromDto_ShouldUpdateEntityFields() {
        //Given
        appointmentMapper.updateEntityFromDto(appointmentDto, appointmentEntity);

        // Then
        assertEquals(appointmentDto.startDate(), appointmentEntity.getStartDate());
        assertEquals(appointmentDto.endDate(), appointmentEntity.getEndDate());
        assertEquals(StatusAppointment.valueOf(appointmentDto.status()), appointmentEntity.getStatus());
        assertEquals(PaymentMethod.valueOf(appointmentDto.paymentMethod()), appointmentEntity.getPaymentMethod());
    }

    @Test
    void toEntity_ShouldReturnEntity_WhenDtoIsValid() {

        // When
        when(userRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(userRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));

        //Ejecutando el metodo del controlador a testear
        AppointmentEntity result = appointmentMapper.toEntity(appointmentDto);

        // Then
        assertNotNull(result);
        assertEquals(appointmentDto.startDate(), result.getStartDate());
        assertEquals(appointmentDto.endDate(), result.getEndDate());
        assertEquals(StatusAppointment.RESERVED, result.getStatus());
        assertEquals(PaymentMethod.CARD, result.getPaymentMethod());
        assertEquals(customer, result.getCustomer());
        assertEquals(employee, result.getEmployee());
        assertEquals(service, result.getService());
    }


}
