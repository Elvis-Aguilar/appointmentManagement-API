package com.appointment.management.domain.service.report;

import com.appointment.management.domain.service.auth.TemplateRendererService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class DownloadPdfService {

    @Autowired
    private TemplateRendererService templateRendererService;

    @Autowired
    private PdfGeneratorService pdfService;

    public ResponseEntity<Resource> downloadPdf(String templateName, Map<String, Object> templateVariables){
        String billHtml = templateRendererService.renderTemplate(templateName, templateVariables);
        try {
            byte[] pdfBytes = pdfService.generatePdfFromHtmlString(billHtml);

            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(resource.contentLength())
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.attachment()
                                    .filename("pdf-test.pdf")
                                    .build()
                                    .toString())
                    .body(resource);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return ResponseEntity.badRequest().build();
        }
    }

    public byte[]  generatePdf(String templateName, Map<String, Object> templateVariables){
        String billHtml = templateRendererService.renderTemplate(templateName, templateVariables);
        try {
            byte[] pdfBytes = pdfService.generatePdfFromHtmlString(billHtml);

            ByteArrayResource resource = new ByteArrayResource(pdfBytes);
            return resource.getByteArray();
        }catch (Exception e){
            return null;
        }
    }
}
