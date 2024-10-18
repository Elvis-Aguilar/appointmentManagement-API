package com.appointment.management.presentation.mapper.helpers;

import com.appointment.management.persistance.entity.UserEntity;
import com.appointment.management.persistance.repository.UserRepository;
import com.appointment.management.presentation.mapper.helpers.UserMapperHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserMapperHelperTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserMapperHelper userMapperHelper;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("user-Test");
    }

    @Test
    void shouldReturnUserWhenIdIsValidAndUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        UserEntity result = userMapperHelper.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("user-Test", result.getName());

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void shouldReturnNullWhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserEntity result = userMapperHelper.findById(1L);

        assertNull(result);

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void shouldReturnNullWhenIdIsNull() {
        UserEntity result = userMapperHelper.findById(null);

        assertNull(result);

        verify(userRepository, never()).findById(any());
    }

    @Test
    void shouldReturnIdWhenUserEntityIsNotNull() {
        Long result = userMapperHelper.toId(userEntity);

        assertNotNull(result);
        assertEquals(1L, result);
    }

    @Test
    void shouldReturnNullWhenUserEntityIsNull() {
        Long result = userMapperHelper.toId(null);

        assertNull(result);
    }
}
