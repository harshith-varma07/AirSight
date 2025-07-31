package com.air.airquality.controller;

import com.air.airquality.dto.AqiResponse;
import com.air.airquality.services.AqiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/export")
@CrossOrigin(origins = "*")
public class DataExportController {
    
    @Autowired
    private AqiService aqiService;
    
    @GetMapping("/csv/{city}")
    public ResponseEntity<?> exportToCsv(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        try {
            List<AqiResponse> data = aqiService.getHistoricalData(city, startDate, endDate);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintWriter writer = new PrintWriter(outputStream);
            
            // CSV Header
            writer.println("Timestamp,City,AQI_Value,AQI_Category,PM2.5,PM10,NO2,SO2,CO,O3");
            
            // CSV Data
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (AqiResponse aqi : data) {
                writer.printf("%s,%s,%d,%s,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f%n",
                    aqi.getTimestamp().format(formatter),
                    aqi.getCity(),
                    aqi.getAqiValue(),
                    aqi.getAqiCategory(),
                    aqi.getPm25() != null ? aqi.getPm25() : 0,
                    aqi.getPm10() != null ? aqi.getPm10() : 0,
                    aqi.getNo2() != null ? aqi.getNo2() : 0,
                    aqi.getSo2() != null ? aqi.getSo2() : 0,
                    aqi.getCo() != null ? aqi.getCo() : 0,
                    aqi.getO3() != null ? aqi.getO3() : 0
                );
            }
            
            writer.flush();
            writer.close();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", 
                String.format("aqi_data_%s_%s_to_%s.csv", 
                    city, 
                    startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                )
            );
            
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error exporting data: " + e.getMessage());
        }
    }
}