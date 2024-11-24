package com.appointment.management.presentation.controller;

import com.appointment.management.domain.dto.appoinment.CancellationSurchargeDto;
import com.appointment.management.domain.dto.business.BusinessConfigurationDto;
import com.appointment.management.domain.dto.report.clienteReportItemDto;
import com.appointment.management.domain.dto.report.clietnReportSendDto;
import com.appointment.management.domain.service.appointmet.CancellationSurchargeService;
import com.appointment.management.domain.service.business.BusinessConfigurationService;
import com.appointment.management.domain.service.report.DownloadExcelService;
import com.appointment.management.domain.service.report.DownloadPdfService;
import com.appointment.management.persistance.enums.StatusCancellation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CancellationSurchargeControllerTest {

    @InjectMocks
    private CancellationSurchargeController controller;

    @Mock
    private CancellationSurchargeService cancellationSurchargeService;

    @Mock
    private DownloadPdfService downloadPdfService;

    @Mock
    private BusinessConfigurationService businessConfigurationService;

    @Mock
    private DownloadExcelService downloadExcelService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        List<CancellationSurchargeDto> mockList = List.of(
                new CancellationSurchargeDto(1L, 101L, LocalDate.now().atStartOfDay(), 201L, StatusCancellation.PENDING),
                new CancellationSurchargeDto(2L, 102L, LocalDate.now().atStartOfDay(), 202L, StatusCancellation.PENDING)
        );

        when(cancellationSurchargeService.getAll()).thenReturn(mockList);

        ResponseEntity<List<CancellationSurchargeDto>> response = controller.findAll();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockList, response.getBody());

        verify(cancellationSurchargeService).getAll();
    }

}
