package com.appointment.management.domain.service.report;


import com.appointment.management.domain.dto.business.BusinessConfigurationDto;
import com.appointment.management.domain.dto.report.ServiceSendDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class DownloadExcelService {

    public ResponseEntity<byte[]> generateExcelReport(List<String> headers, List<Object> salesData, String nameReport,
                                                      BusinessConfigurationDto busines, String title, String filtro, String range, Integer size) throws IOException {
        // Crear un libro de Excel (Workbook)
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(nameReport);

        // Estilo para el título principal
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);

        // Título principal
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(title);
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.size() - 1));

        // Detalles del reporte
        Row detailsRow = sheet.createRow(1);
        detailsRow.createCell(0).setCellValue(busines.name());

        Row dateRow = sheet.createRow(3);
        dateRow.createCell(0).setCellValue("Fecha del reporte: " + LocalDate.now());
        dateRow.createCell(1).setCellValue("Tipo Reporte: Reporte de Ventas/citas/clientes");

        Row periodRow = sheet.createRow(4);
        periodRow.createCell(0).setCellValue("Periodo: "+range);

        Row filtersRow = sheet.createRow(5);
        filtersRow.createCell(0).setCellValue("Cantidad: " + size);
        filtersRow.createCell(1).setCellValue("Tipo Criterio: "+filtro);

        // Espacio entre el encabezado personalizado y la tabla de datos
        int startRow = 7;

        // Crear estilo para el encabezado de la tabla de datos
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // Encabezado de la tabla de datos
        Row headerRow = sheet.createRow(startRow);
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(headerStyle);
        }

        // Llenado de los datos en las filas de la tabla
        int rowNum = startRow + 1;
        int colum = 0;
        Row row = sheet.createRow(rowNum++);
        for (Object sale : salesData) {
            if (colum == headers.size()) {
                row = sheet.createRow(rowNum++);
                colum = 0;
            }
            row.createCell(colum).setCellValue(sale.toString());
            colum++;
        }

        // Ajustar el tamaño de las columnas
        for (int i = 0; i < headers.size(); i++) {
            sheet.autoSizeColumn(i);
        }

        return downloadExcelReport(workbook, nameReport);
    }

    private ResponseEntity<byte[]> downloadExcelReport(Workbook workbook, String nameReport) throws IOException {
        // Escribir los datos a un ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        // Preparar la respuesta HTTP con el archivo Excel
        HttpHeaders headersHttp = new HttpHeaders();
        headersHttp.setContentDispositionFormData("attachment", nameReport + ".xlsx");
        headersHttp.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headersHttp)
                .body(outputStream.toByteArray());
    }
}

