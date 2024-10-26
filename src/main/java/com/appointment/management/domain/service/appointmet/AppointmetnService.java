package com.appointment.management.domain.service.appointmet;

import com.appointment.management.application.exception.RequestConflictException;
import com.appointment.management.domain.dto.appoinment.AppointmentDto;
import com.appointment.management.domain.dto.business.BusinessConfigurationDto;
import com.appointment.management.domain.dto.business.ServiceDto;
import com.appointment.management.domain.dto.user.UserDto;
import com.appointment.management.domain.service.UserService;
import com.appointment.management.domain.service.auth.EmailService;
import com.appointment.management.domain.service.auth.TemplateRendererService;
import com.appointment.management.domain.service.business.BusinessConfigurationService;
import com.appointment.management.domain.service.business.ServiceService;
import com.appointment.management.persistance.entity.AppointmentEntity;
import com.appointment.management.persistance.entity.BusinessConfigurationEntity;
import com.appointment.management.persistance.entity.ServiceEntity;
import com.appointment.management.persistance.entity.UserEntity;
import com.appointment.management.persistance.enums.StatusAppointment;
import com.appointment.management.persistance.repository.AppointmentRepository;
import com.appointment.management.presentation.mapper.business.AppointmentMapper;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmetnService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private BusinessConfigurationService businessConfigurationService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TemplateRendererService templateRendererService;

    @Autowired
    private UserService userService;

    @Autowired
    private ServiceService serviceService;

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

    public AppointmentDto completedAppointment(Long id) {
        AppointmentEntity existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        existingAppointment.setStatus(StatusAppointment.COMPLETED);
        AppointmentEntity updatedEntity = appointmentRepository.save(existingAppointment);

        /*logica de envio de factura*/
        BusinessConfigurationDto busines = this.businessConfigurationService.findFirst();
        LocalDate date = LocalDate.now();
        UserDto user = userService.findUserById(existingAppointment.getCustomer().getId()).orElseThrow();
        ServiceDto service = this.serviceService.getServiceById(existingAppointment.getService().getId());
        LocalDate dateService = existingAppointment.getStartDate().toLocalDate();

        BigDecimal price = service.price();
        BigDecimal additionalAmount = new BigDecimal("15.00");

        BigDecimal newPrice = price.add(additionalAmount);
        Map<String, Object> templateVariables = Map.of(
                "company", busines,
                "order_date",date,
                "client", user,
                "servicio", service.name(),
                "fecha",dateService,
                "price", service.price(),
                "priceTotal",newPrice
        );

        String confirmationHtml = templateRendererService.renderTemplate("bill", templateVariables);

        try {
            emailService.sendHtmlEmail("Appointment-Management", user.email(),
                    "Facturacion ", confirmationHtml);
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
            throw new RequestConflictException("No se pudo enviar el correo de confirmacion "+e.getMessage());
        }

        return appointmentMapper.toDto(updatedEntity);

    }
}
