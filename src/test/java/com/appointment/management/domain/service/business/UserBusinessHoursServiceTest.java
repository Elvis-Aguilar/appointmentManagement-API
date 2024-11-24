package com.appointment.management.domain.service.business;

import com.appointment.management.domain.dto.business.UpdateUserBusinessHours;
import com.appointment.management.domain.dto.business.UserHoursDto;
import com.appointment.management.persistance.entity.BusinessHoursEntity;
import com.appointment.management.persistance.entity.RoleEntity;
import com.appointment.management.persistance.entity.UserBusinessHoursEntity;
import com.appointment.management.persistance.entity.UserEntity;
import com.appointment.management.persistance.repository.BusinessHoursRepository;
import com.appointment.management.persistance.repository.UserBusinessHoursRepository;
import com.appointment.management.persistance.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserBusinessHoursServiceTest {

    @Mock
    private UserBusinessHoursRepository userBusinessHoursRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BusinessHoursRepository businessHoursRepository;

    @InjectMocks
    private UserBusinessHoursService userBusinessHoursService;

    @Captor
    private ArgumentCaptor<UserBusinessHoursEntity> userBusinessHoursCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test: updateUserBusinessHours - Caso exitoso
    @Test
    void givenValidUpdateAndBusinessHours_whenUpdateUserBusinessHours_thenSaveEntities() {
        // Given
        Long businessHoursId = 1L;
        List<Long> userIds = List.of(101L, 102L);
        UpdateUserBusinessHours updateUserBusinessHours = new UpdateUserBusinessHours(userIds);

        BusinessHoursEntity businessHours = new BusinessHoursEntity();
        businessHours.setId(businessHoursId);

        UserEntity user1 = new UserEntity("John Doe", "123456", "password", "1234567890123", "john@example.com", "555-1234", new RoleEntity( "Admin","fadfadf"));
        user1.setId(101L);

        UserEntity user2 = new UserEntity("Jane Doe", "654321", "password", "9876543210123", "jane@example.com", "555-5678", new RoleEntity( "Admin","fadfadf"));
        user2.setId(102L);

        when(businessHoursRepository.findById(businessHoursId)).thenReturn(Optional.of(businessHours));
        when(userRepository.findById(101L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(102L)).thenReturn(Optional.of(user2));

        // When
        userBusinessHoursService.updateUserBusinessHours(updateUserBusinessHours, businessHoursId);

        // Then
        verify(userBusinessHoursRepository).deleteAllByBusinessHoursId(businessHoursId);
        verify(userBusinessHoursRepository, times(2)).save(userBusinessHoursCaptor.capture());

        List<UserBusinessHoursEntity> capturedEntities = userBusinessHoursCaptor.getAllValues();
        assertEquals(2, capturedEntities.size());
        assertEquals(user1, capturedEntities.get(0).getUser());
        assertEquals(businessHours, capturedEntities.get(0).getBusinessHours());
        assertEquals(user2, capturedEntities.get(1).getUser());
        assertEquals(businessHours, capturedEntities.get(1).getBusinessHours());
    }

    // Test: updateUserBusinessHours - Caso de negocio no encontrado
    @Test
    void givenInvalidBusinessHoursId_whenUpdateUserBusinessHours_thenThrowException() {
        // Given
        Long businessHoursId = 1L;
        when(businessHoursRepository.findById(businessHoursId)).thenReturn(Optional.empty());

        // When / Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userBusinessHoursService.updateUserBusinessHours(new UpdateUserBusinessHours(new ArrayList<>()), businessHoursId)
        );
        assertEquals("role not found", exception.getMessage());
        verify(userBusinessHoursRepository, never()).deleteAllByBusinessHoursId(anyLong());
        verify(userBusinessHoursRepository, never()).save(any());
    }

    // Test: updateUserBusinessHours - Caso de usuario no encontrado
    @Test
    void givenInvalidUserId_whenUpdateUserBusinessHours_thenThrowException() {
        // Given
        Long businessHoursId = 1L;
        Long invalidUserId = 101L;
        UpdateUserBusinessHours updateUserBusinessHours = new UpdateUserBusinessHours(List.of(invalidUserId));

        BusinessHoursEntity businessHours = new BusinessHoursEntity();
        businessHours.setId(businessHoursId);

        when(businessHoursRepository.findById(businessHoursId)).thenReturn(Optional.of(businessHours));
        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        // When / Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userBusinessHoursService.updateUserBusinessHours(updateUserBusinessHours, businessHoursId)
        );
        assertEquals("user not found", exception.getMessage());
        verify(userBusinessHoursRepository, never()).save(any());
    }

    // Test: getUserBusinessHours - Caso exitoso
    @Test
    void givenValidBusinessHoursId_whenGetUserBusinessHours_thenReturnDtoList() {
        // Given
        Long businessHoursId = 1L;
        UserEntity user1 = new UserEntity("John Doe", "123456", "password", "1234567890123", "john@example.com", "555-1234", new RoleEntity( "Admin","fadfadf"));
        user1.setId(101L);

        UserEntity user2 = new UserEntity("Jane Doe", "654321", "password", "9876543210123", "jane@example.com", "555-5678", new RoleEntity( "Admin","fadfadf"));
        user2.setId(102L);

        UserBusinessHoursEntity userBusinessHours1 = new UserBusinessHoursEntity(user1, new BusinessHoursEntity());
        UserBusinessHoursEntity userBusinessHours2 = new UserBusinessHoursEntity(user2, new BusinessHoursEntity());

        when(userBusinessHoursRepository.findAllByBusinessHoursId(businessHoursId))
                .thenReturn(List.of(userBusinessHours1, userBusinessHours2));

        // When
        List<UserHoursDto> userHoursDtos = userBusinessHoursService.getUserBusinessHours(businessHoursId);

        // Then
        assertEquals(2, userHoursDtos.size());
        assertEquals(user1.getId(), userHoursDtos.get(0).id());
        assertEquals(user1.getName(), userHoursDtos.get(0).name());
        assertEquals(user1.getEmail(), userHoursDtos.get(0).email());

        assertEquals(user2.getId(), userHoursDtos.get(1).id());
        assertEquals(user2.getName(), userHoursDtos.get(1).name());
        assertEquals(user2.getEmail(), userHoursDtos.get(1).email());
    }

    // Test: getUserBusinessHours - Sin resultados
    @Test
    void givenNoUsersForBusinessHours_whenGetUserBusinessHours_thenReturnEmptyList() {
        // Given
        Long businessHoursId = 1L;
        when(userBusinessHoursRepository.findAllByBusinessHoursId(businessHoursId)).thenReturn(new ArrayList<>());

        // When
        List<UserHoursDto> userHoursDtos = userBusinessHoursService.getUserBusinessHours(businessHoursId);

        // Then
        assertNotNull(userHoursDtos);
        assertTrue(userHoursDtos.isEmpty());
    }
}
