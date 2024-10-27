package com.appointment.management.presentation.controller;

import com.appointment.management.domain.dto.appoinment.CancellationSurchargeDto;
import com.appointment.management.domain.dto.business.BusinessConfigurationDto;
import com.appointment.management.domain.dto.report.*;
import com.appointment.management.domain.service.appointmet.CancellationSurchargeService;
import com.appointment.management.domain.service.business.BusinessConfigurationService;
import com.appointment.management.domain.service.report.DownloadExcelService;
import com.appointment.management.domain.service.report.DownloadPdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cancellation")
public class CancellationSurchargeController {

    @Autowired
    private CancellationSurchargeService cancellationSurchargeService;

    @Autowired
    private DownloadPdfService downloadPdfService;

    @Autowired
    private BusinessConfigurationService businessConfigurationService;

    @Autowired
    private DownloadExcelService downloadExcelService;


    @GetMapping
    public ResponseEntity<List<CancellationSurchargeDto>> findAll(){
        List<CancellationSurchargeDto> list = this.cancellationSurchargeService.getAll();
        return ResponseEntity.ok().body(list);
    }


    @PostMapping("/downloadPDF")
    public ResponseEntity<Resource> downloadReport(@RequestBody clietnReportSendDto dto) {
        BusinessConfigurationDto busines = this.businessConfigurationService.findFirst();

        Map<String, Object> templateVariables = Map.of(
                "items",dto.items(),
                "size", dto.items().size(),
                "filter", dto.filtro(),
                "rangeDate", dto.rangeDate(),
                "dateReport", LocalDate.now(),
                "nameCompany", busines.name(),
                "companyLogo", busines.logoUrl()
        );
        return this.downloadPdfService.downloadPdf("report-customers", templateVariables);
    }

    @PostMapping("/download-excel")
    ResponseEntity<byte[]>downloadReportExcel(@RequestBody clietnReportSendDto dto)throws IOException {
        BusinessConfigurationDto busines = this.businessConfigurationService.findFirst();
        List<clienteReportItemDto> items = dto.items();
        List<String> headers = new ArrayList<>();
        headers.add("Nombre");
        headers.add("email");
        headers.add("cui");
        headers.add("Cantidad citas");
        List<Object> userObjects = new ArrayList<>();
        for (clienteReportItemDto item : items) {
            userObjects.add(item.Nombre() == null ? "" : item.Nombre() );
            userObjects.add(item.email() == null ? "" : item.email());
            userObjects.add(item.cui() == null ? "" : item.cui() );
            userObjects.add(item.Cantidad() == null ? "" : item.Cantidad());
        }

        return this.downloadExcelService.generateExcelReport(headers, userObjects, "reporte_Clientes", busines, "Reporte Clientes", dto.filtro(), dto.rangeDate(), dto.items().size());
    }
}
