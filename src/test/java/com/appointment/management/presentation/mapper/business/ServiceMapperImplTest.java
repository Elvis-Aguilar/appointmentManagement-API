package com.appointment.management.presentation.mapper.business;

import com.appointment.management.domain.dto.business.ServiceDto;
import com.appointment.management.persistance.entity.ServiceEntity;
import com.appointment.management.persistance.enums.StatusBusinessHours;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ServiceMapperImplTest {

    @InjectMocks
    private ServiceMapperImpl serviceMapperImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnNullWhenDtoIsNullInToEntity() {
        ServiceEntity result = serviceMapperImpl.toEntity(null);

        assertNull(result);
    }

    @Test
    void shouldMapDtoToEntitySuccessfully() {
        ServiceDto dto = new ServiceDto(
                1L,
                "Test Service",
                BigDecimal.valueOf(100.00),
                LocalTime.of(1, 30),
                "Test description",
                10,
                "Test location",
                "http://test.com/image.jpg",
                "AVAILABLE"
        );

        ServiceEntity entity = serviceMapperImpl.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.id(), entity.getId());
        assertEquals(dto.name(), entity.getName());
        assertEquals(dto.price(), entity.getPrice());
        assertEquals(dto.duration(), entity.getDuration());
        assertEquals(dto.description(), entity.getDescription());
        assertEquals(dto.peopleReaches(), entity.getPeopleReaches());
        assertEquals(dto.location(), entity.getLocation());
        assertEquals(dto.imageUrl(), entity.getImageUrl());
    }

    @Test
    void shouldReturnNullWhenEntityIsNullInToDto() {
        ServiceDto result = serviceMapperImpl.toDto(null);

        assertNull(result);
    }

    @Test
    void shouldMapEntityToDtoSuccessfully() {
        ServiceEntity entity = new ServiceEntity();
        entity.setId(1L);
        entity.setName("Test Service");
        entity.setPrice(BigDecimal.valueOf(100.00));
        entity.setDuration(LocalTime.of(1, 30));
        entity.setDescription("Test description");
        entity.setPeopleReaches(10);
        entity.setLocation("Test location");
        entity.setImageUrl("http://test.com/image.jpg");
        entity.setStatus(StatusBusinessHours.AVAILABLE);

        ServiceDto dto = serviceMapperImpl.toDto(entity);

        assertNotNull(dto);
        assertEquals(entity.getId(), dto.id());
        assertEquals(entity.getName(), dto.name());
        assertEquals(entity.getPrice(), dto.price());
        assertEquals(entity.getDuration(), dto.duration());
        assertEquals(entity.getDescription(), dto.description());
        assertEquals(entity.getPeopleReaches(), dto.peopleReaches());
        assertEquals(entity.getLocation(), dto.location());
        assertEquals(entity.getImageUrl(), dto.imageUrl());
        assertEquals(entity.getStatus().name(), dto.status());
    }

    @Test
    void shouldDoNothingWhenDtoIsNullInUpdateEntityFromDto() {
        ServiceEntity entity = new ServiceEntity();
        entity.setName("Initial Name");

        serviceMapperImpl.updateEntityFromDto(null, entity);

        assertEquals("Initial Name", entity.getName());  // No debe cambiar el nombre
    }

    @Test
    void shouldUpdateEntityFromDtoSuccessfully() {
        ServiceEntity entity = new ServiceEntity();
        entity.setStatus(StatusBusinessHours.AVAILABLE);  // Valor inicial

        ServiceDto dto = new ServiceDto(
                1L,
                "Updated Service",
                BigDecimal.valueOf(150.00),
                LocalTime.of(2, 0),
                "Updated description",
                20,
                "Updated location",
                "http://test.com/updated-image.jpg",
                "DELETED"
        );

        serviceMapperImpl.updateEntityFromDto(dto, entity);

        assertEquals(dto.name(), entity.getName());
        assertEquals(dto.price(), entity.getPrice());
        assertEquals(dto.duration(), entity.getDuration());
        assertEquals(dto.description(), entity.getDescription());
        assertEquals(dto.peopleReaches(), entity.getPeopleReaches());
        assertEquals(dto.location(), entity.getLocation());
        assertEquals(dto.imageUrl(), entity.getImageUrl());
        assertEquals(StatusBusinessHours.DELETED, entity.getStatus());
    }

    @Test
    void shouldSetStatusNullWhenDtoStatusIsNull() {
        ServiceEntity entity = new ServiceEntity();
        entity.setStatus(StatusBusinessHours.AVAILABLE);  // Valor inicial

        ServiceDto dto = new ServiceDto(
                1L,
                "Test Service",
                BigDecimal.valueOf(100.00),
                LocalTime.of(1, 30),
                "Test description",
                10,
                "Test location",
                "http://test.com/image.jpg",
                null  // El estatus es nulo en el DTO
        );

        serviceMapperImpl.updateEntityFromDto(dto, entity);

        assertNull(entity.getStatus());  // El estatus debe quedar en null
    }
}
