package com.appointment.management.domain.service.appointmet;

import com.appointment.management.domain.dto.appoinment.AppointmentDto;
import com.appointment.management.persistance.entity.AppointmentEntity;
import com.appointment.management.persistance.repository.AppointmentRepository;
import com.appointment.management.presentation.mapper.business.AppointmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmetnService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentMapper appointmentMapper;


    @Transactional(readOnly = true)
    public List<AppointmentDto> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(appointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<AppointmentDto> getAppointmentById(Long id) {
        return appointmentRepository.findById(id).map(appointmentMapper::toDto);
    }

    @Transactional
    public AppointmentDto createAppointment(AppointmentDto appointmentDto) {
        // Verificamos si el empleado tiene una cita en el rango de fechas
        boolean isOverlapping = appointmentRepository.existsByEmployeeIdAndDateRange(
                appointmentDto.employeeId(),
                appointmentDto.startDate(),
                appointmentDto.endDate()
        );

        if (isOverlapping) {
            throw new IllegalArgumentException("El empleado ya tiene una cita en ese rango de fechas. y hora");
        }

        // Si no hay conflicto, procedemos a crear la cita
        AppointmentEntity entity = appointmentMapper.toEntity(appointmentDto);
        AppointmentEntity savedEntity = appointmentRepository.save(entity);
        return appointmentMapper.toDto(savedEntity);
    }

    @Transactional
    public AppointmentDto updateAppointment(Long id, AppointmentDto appointmentDto) {
        AppointmentEntity existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        appointmentMapper.updateEntityFromDto(appointmentDto, existingAppointment);

        AppointmentEntity updatedEntity = appointmentRepository.save(existingAppointment);
        return appointmentMapper.toDto(updatedEntity);
    }

    @Transactional
    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new IllegalArgumentException("Appointment not found");
        }
        appointmentRepository.deleteById(id);
    }
}
