package com.appointment.management.presentation.controller;

import com.appointment.management.domain.dto.appoinment.CancellationSurchargeDto;
import com.appointment.management.domain.dto.business.BusinessConfigurationDto;
import com.appointment.management.domain.dto.report.*;
import com.appointment.management.domain.service.appointmet.CancellationSurchargeService;
import com.appointment.management.domain.service.business.BusinessConfigurationService;
import com.appointment.management.domain.service.report.DownloadExcelService;
import com.appointment.management.domain.service.report.DownloadPdfService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cancellation")
public class CancellationSurchargeController {

    @Autowired
    private CancellationSurchargeService cancellationSurchargeService;

    @GetMapping
    public ResponseEntity<List<CancellationSurchargeDto>> findAll(){
        List<CancellationSurchargeDto> list = this.cancellationSurchargeService.getAll();
        return ResponseEntity.ok().body(list);
    }

}
