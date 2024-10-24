package com.appointment.management.presentation.mapper.business;

import com.appointment.management.domain.dto.appoinment.AppointmentDto;
import com.appointment.management.persistance.entity.AppointmentEntity;
import com.appointment.management.persistance.entity.ServiceEntity;
import com.appointment.management.persistance.entity.UserEntity;
import com.appointment.management.persistance.enums.PaymentMethod;
import com.appointment.management.persistance.enums.StatusAppointment;
import com.appointment.management.persistance.repository.ServiceRepository;
import com.appointment.management.persistance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AppointmentMapper {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    // Convierte de AppointmentDto a AppointmentEntity
    public AppointmentEntity toEntity(AppointmentDto dto) {
        System.out.println(dto.toString());
        UserEntity customer = this.userRepository.findById(dto.customer()).orElse(null);
        ServiceEntity service = this.serviceRepository.findById(dto.service()).orElse(null);
        UserEntity employee = this.userRepository.findById(dto.employeeId()).orElse(null);

        assert service != null;
        assert customer != null;
        assert employee != null;
        return new AppointmentEntity(
                customer,
                service,
                employee,
                dto.startDate(),
                dto.endDate(),
                StatusAppointment.valueOf(dto.status()),
                PaymentMethod.valueOf(dto.paymentMethod())
        );
    }

    public AppointmentDto toDto(AppointmentEntity entity) {
        return new AppointmentDto(
                entity.getId(),
                entity.getCustomer().getId(),
                entity.getService().getId(),
                entity.getEmployee().getId(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getStatus().name(),
                entity.getPaymentMethod().name()
        );
    }

    public void updateEntityFromDto(AppointmentDto dto, AppointmentEntity entity) {
        entity.setStartDate(dto.startDate());
        entity.setEndDate(dto.endDate());
        entity.setStatus(StatusAppointment.valueOf(dto.status()));
        entity.setPaymentMethod(PaymentMethod.valueOf(dto.paymentMethod()));
    }
}

