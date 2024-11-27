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
import com.appointment.management.persistance.entity.*;
import com.appointment.management.persistance.enums.StatusAppointment;
import com.appointment.management.persistance.enums.StatusCancellation;
import com.appointment.management.persistance.repository.AppointmentRepository;
import com.appointment.management.presentation.mapper.business.AppointmentMapper;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
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

    @Autowired
    private  CancellationSurchargeService cancellationSurchargeService;

    @Transactional(readOnly = true)
    public List<AppointmentDto> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(appointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentDto> getAllAppointmentsByEmployeeId(Long employeeId) {
        return appointmentRepository.findByEmployeeId(employeeId).stream()
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
        //agrega la multa
        Optional<AppointmentEntity> latestAppointment = appointmentRepository.findFirstByCustomerIdOrderByIdDesc(entity.getCustomer().getId());
        if (latestAppointment.isPresent() && latestAppointment.get().isFine()) {
            entity.setFine(true);
        }
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

        String oter = updatedEntity.isFine() ? "Multa por cancelar fuera del tiempo permitido y mal uso de la aplicacion": "--";
        BigDecimal fine = updatedEntity.isFine() ? busines.cancellationSurcharge() : new BigDecimal("0.00");

        BigDecimal total = fine.add(service.price());

        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("company", busines);
        templateVariables.put("order_date", date);
        templateVariables.put("client", user);
        templateVariables.put("servicio", service.name());
        templateVariables.put("fecha", dateService);
        templateVariables.put("price", service.price());
        templateVariables.put("priceTotal", total);
        templateVariables.put("oter", oter);
        templateVariables.put("fine", fine);

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

    public AppointmentDto canceledAppointment(Long id) {
        AppointmentEntity existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        existingAppointment.setStatus(StatusAppointment.CANCELED);
        existingAppointment.setFine(true);

        AppointmentEntity updatedEntity = appointmentRepository.save(existingAppointment);

        /*logica de notificacion y agregar a lista malos usuarios*/

        /* agregar a lista de malos empleados**/
        UserEntity userEntity = userService.findUserByIdEntity(existingAppointment.getCustomer().getId());
        CancellationSurchargeEntity cancellationSurchargeEntity = new CancellationSurchargeEntity(existingAppointment, userEntity, StatusCancellation.PENDING);
        this.cancellationSurchargeService.create(cancellationSurchargeEntity);

        /*logica de envio de notificacion al correo*/
        BusinessConfigurationDto busines = this.businessConfigurationService.findFirst();
        LocalDate date = LocalDate.now();
        UserDto user = userService.findUserById(existingAppointment.getCustomer().getId()).orElseThrow();
        ServiceDto service = this.serviceService.getServiceById(existingAppointment.getService().getId());
        LocalDate dateService = existingAppointment.getStartDate().toLocalDate();

        /*logica de envio de notificacion al correo*/
        Map<String, Object> templateVariables = Map.of(
                "company", busines,
                "order_date",date,
                "client", user,
                "servicio", service.name(),
                "fecha",dateService,
                "price", service.price(),
                "mora",busines.cancellationSurcharge()
        );

        String confirmationHtml = templateRendererService.renderTemplate("notify-bad-customer", templateVariables);

        try {
            emailService.sendHtmlEmail("Appointment-Management", user.email(),
                    "Notificacion Mal Uso ", confirmationHtml);
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
            throw new RequestConflictException("No se pudo enviar el correo de confirmacion "+e.getMessage());
        }

        return appointmentMapper.toDto(updatedEntity);

    }

    public AppointmentDto stateCancelAppointment(Long id) {
        // Buscar la cita por su ID
        AppointmentEntity existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        // Cambiar el estado de la cita a "CANCELED"
        existingAppointment.setStatus(StatusAppointment.CANCELED);

        // Guardar la cita actualizada en la base de datos
        AppointmentEntity updatedEntity = appointmentRepository.save(existingAppointment);

        // Retornar el DTO de la cita actualizada
        return appointmentMapper.toDto(updatedEntity);
    }

}
