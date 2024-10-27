package com.appointment.management.domain.service.appointmet;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
import com.appointment.management.persistance.entity.ServiceEntity;
import com.appointment.management.persistance.entity.UserEntity;
import com.appointment.management.persistance.enums.StatusAppointment;
import com.appointment.management.persistance.repository.AppointmentRepository;
import com.appointment.management.presentation.mapper.business.AppointmentMapper;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentMapper appointmentMapper;

    @Mock
    private BusinessConfigurationService businessConfigurationService;

    @Mock
    private EmailService emailService;

    @Mock
    private TemplateRendererService templateRendererService;

    @Mock
    private UserService userService;

    @Mock
    private ServiceService serviceService;

    @InjectMocks
    private AppointmetnService appointmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllAppointments() {
        // Given
        AppointmentEntity appointmentEntity = new AppointmentEntity();
        AppointmentDto appointmentDto = new AppointmentDto(1L, 2L, 2L, 3L, LocalDateTime.now(), LocalDateTime.now(), "ffd", "fasdfas");

        when(appointmentRepository.findAll()).thenReturn(List.of(appointmentEntity));
        when(appointmentMapper.toDto(appointmentEntity)).thenReturn(appointmentDto);

        // When
        List<AppointmentDto> result = appointmentService.getAllAppointments();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(appointmentDto, result.get(0));
        verify(appointmentRepository).findAll();
        verify(appointmentMapper).toDto(appointmentEntity);
    }

    @Test
    void testGetAppointmentById() {
        // Given
        Long appointmentId = 1L;
        AppointmentEntity appointmentEntity = new AppointmentEntity(); // Asigna valores según la entidad
        AppointmentDto appointmentDto = new AppointmentDto(1L, 2L, 2L, 3L, LocalDateTime.now(), LocalDateTime.now(), "ffd", "fasdfas");

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointmentEntity));
        when(appointmentMapper.toDto(appointmentEntity)).thenReturn(appointmentDto);

        // When
        Optional<AppointmentDto> result = appointmentService.getAppointmentById(appointmentId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(appointmentDto, result.get());
        verify(appointmentRepository).findById(appointmentId);
    }

    @Test
    void testCreateAppointment() {
        // Given
        AppointmentDto appointmentDto = new AppointmentDto(1L, 2L, 2L, 3L, LocalDateTime.now(), LocalDateTime.now(), "ffd", "fasdfas");
        AppointmentEntity appointmentEntity = new AppointmentEntity();
        AppointmentEntity savedEntity = new AppointmentEntity();

        when(appointmentMapper.toEntity(appointmentDto)).thenReturn(appointmentEntity);
        when(appointmentRepository.existsByEmployeeIdAndDateRange(any(), any(), any())).thenReturn(false);
        when(appointmentRepository.save(appointmentEntity)).thenReturn(savedEntity);
        when(appointmentMapper.toDto(savedEntity)).thenReturn(appointmentDto);

        // When
        AppointmentDto result = appointmentService.createAppointment(appointmentDto);

        // Then
        assertNotNull(result);
        verify(appointmentRepository).existsByEmployeeIdAndDateRange(any(), any(), any());
        verify(appointmentRepository).save(appointmentEntity);
        verify(appointmentMapper).toEntity(appointmentDto);
        verify(appointmentMapper).toDto(savedEntity);
    }

    @Test
    void testUpdateAppointment() {
        // Given
        Long appointmentId = 1L;
        AppointmentDto appointmentDto = new AppointmentDto(1L, 2L, 2L, 3L, LocalDateTime.now(), LocalDateTime.now(), "ffd", "fasdfas");
        AppointmentEntity existingAppointment = new AppointmentEntity(); // Asegúrate de inicializar la entidad según sea necesario

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));

        // Aquí no necesitas el return ya que el método es void
        doNothing().when(appointmentMapper).updateEntityFromDto(appointmentDto, existingAppointment);

        when(appointmentRepository.save(existingAppointment)).thenReturn(existingAppointment);
        when(appointmentMapper.toDto(existingAppointment)).thenReturn(appointmentDto);

        // When
        AppointmentDto result = appointmentService.updateAppointment(appointmentId, appointmentDto);

        // Then
        assertNotNull(result);
        verify(appointmentRepository).findById(appointmentId);
        verify(appointmentMapper).updateEntityFromDto(appointmentDto, existingAppointment);
        verify(appointmentRepository).save(existingAppointment);
    }

    @Test
    void testDeleteAppointment() {
        // Given
        Long appointmentId = 1L;

        when(appointmentRepository.existsById(appointmentId)).thenReturn(true);

        // When
        assertDoesNotThrow(() -> appointmentService.deleteAppointment(appointmentId));

        // Then
        verify(appointmentRepository).deleteById(appointmentId);
    }

    @Test
    void testDeleteAppointment_NotFound() {
        // Given
        Long appointmentId = 1L;

        when(appointmentRepository.existsById(appointmentId)).thenReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> appointmentService.deleteAppointment(appointmentId));
        verify(appointmentRepository).existsById(appointmentId);
    }

    @Test
    void testCompletedAppointment() {
        // Given
        Long appointmentId = 1L;
        AppointmentEntity existingAppointment = new AppointmentEntity();
        existingAppointment.setStatus(StatusAppointment.COMPLETED);
        existingAppointment.setCustomer(new UserEntity());
        existingAppointment.setService(new ServiceEntity());
        existingAppointment.setStartDate(LocalDateTime.now());

        UserDto userDto = new UserDto(1L, "testUser", "test@example.com", "USER", "fsd", "555", LocalDateTime.now(), "fadsf", false, "adfd", new ArrayList<>());

        ServiceDto serviceDto = new ServiceDto(1L, "fadf", BigDecimal.valueOf(10), LocalTime.now(), "Fad", 5, "fadf","Fadf","COMPLETED"); // Simulación de servicio

        BusinessConfigurationDto businessConfigDto = new BusinessConfigurationDto("Test Company", "http://logo.url");

        // Simulaciones de retorno
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        when(appointmentRepository.save(existingAppointment)).thenReturn(existingAppointment);
        when(businessConfigurationService.findFirst()).thenReturn(businessConfigDto);
        when(userService.findUserById(existingAppointment.getCustomer().getId())).thenReturn(Optional.of(userDto));
        when(serviceService.getServiceById(existingAppointment.getService().getId())).thenReturn(serviceDto);
        when(templateRendererService.renderTemplate(anyString(), anyMap())).thenReturn("<html>...</html>"); // Simulando la plantilla HTML

        // When
        AppointmentDto result = appointmentService.completedAppointment(appointmentId);

        // Then
    }

    @Test
    void testCompletedAppointment_NotFound() {
        // Given
        Long appointmentId = 1L;

        // Simulación de la excepción
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> appointmentService.completedAppointment(appointmentId));
        verify(appointmentRepository).findById(appointmentId);
    }

    @Test
    void testCanceledAppointment() {
        // Given
        Long appointmentId = 1L;
        AppointmentEntity existingAppointment = new AppointmentEntity();
        existingAppointment.setStatus(StatusAppointment.CANCELED); // Estado inicial
        existingAppointment.setCustomer(new UserEntity()); // Simulación de cliente
        existingAppointment.setService(new ServiceEntity()); // Simulación de servicio
        existingAppointment.setStartDate(LocalDateTime.now()); // Establecer fecha y hora

        UserEntity userEntity = new UserEntity(); // Simulación de usuario
        userEntity.setId(existingAppointment.getCustomer().getId());

        UserDto userDto = new UserDto(1L, "testUser", "test@example.com", "USER", "fsd", "555", LocalDateTime.now(), "fadsf", false, "adfd", new ArrayList<>());

        ServiceDto serviceDto = new ServiceDto(1L, "fadf", BigDecimal.valueOf(10), LocalTime.now(), "Fad", 5, "fadf","Fadf","COMPLETED"); // Simulación de servicio

        BusinessConfigurationDto businessConfigDto = new BusinessConfigurationDto("Test Company", "http://logo.url");

        // Simulaciones de retorno
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        when(appointmentRepository.save(existingAppointment)).thenReturn(existingAppointment);
        when(userService.findUserByIdEntity(existingAppointment.getCustomer().getId())).thenReturn(userEntity);
        when(businessConfigurationService.findFirst()).thenReturn(businessConfigDto);
        when(userService.findUserById(existingAppointment.getCustomer().getId())).thenReturn(Optional.of(userDto));
        when(serviceService.getServiceById(existingAppointment.getService().getId())).thenReturn(serviceDto);
        when(templateRendererService.renderTemplate(anyString(), anyMap())).thenReturn("<html>...</html>"); // Simulando la plantilla HTML

        // When

        // Then
        assertEquals(StatusAppointment.CANCELED, existingAppointment.getStatus()); // Verificar el estado
    }

    @Test
    void testCanceledAppointment_NotFound() {
        // Given
        Long appointmentId = 1L;

        // Simulación de la excepción
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> appointmentService.canceledAppointment(appointmentId));
        verify(appointmentRepository).findById(appointmentId);
    }





}
