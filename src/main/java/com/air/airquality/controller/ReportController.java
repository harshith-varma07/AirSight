package com.air.airquality.controller;

import com.air.airquality.model.AirQualityData;
import com.air.airquality.repository.AirQualityRepository;
import com.air.airquality.services.PdfService;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private AirQualityRepository airRepo;

    @Autowired
    private PdfService pdfService;

    @GetMapping("/pdf")
    public ResponseEntity<InputStreamResource> downloadPdf(@RequestParam String city) throws DocumentException {
        // Make sure this method exists in your repository!
        List<AirQualityData> dataList = airRepo.findTop30ByCityOrderByTimestampDesc(city);

        ByteArrayInputStream bis = pdfService.generateAQIReport(dataList, city);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=aqi_report_" + city + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}