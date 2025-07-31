package com.air.airquality.services;

import com.air.airquality.dto.OpenAQResponse;
import com.air.airquality.model.AqiData;
import com.air.airquality.repository.AqiDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class OpenAQService {
    
    @Autowired
    private AqiDataRepository aqiDataRepository;
    
    @Value("${openaq.api.url}")
    private String openAQApiUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    @SuppressWarnings("null")
    public void fetchAndStoreAqiData(String city) {
        try {
            String url = openAQApiUrl + "?city=" + city + "&limit=1";
            ResponseEntity<OpenAQResponse> response = restTemplate.getForEntity(url, OpenAQResponse.class);
            
            if (response.getBody() != null && !response.getBody().getResults().isEmpty()) {
                OpenAQResponse.OpenAQResult result = response.getBody().getResults().get(0);
                
                Map<String, Double> pollutants = new HashMap<>();
                for (OpenAQResponse.Measurement measurement : result.getMeasurements()) {
                    pollutants.put(measurement.getParameter().toLowerCase(), measurement.getValue());
                }
                
                // Calculate AQI based on PM2.5 (simplified calculation)
                Integer aqiValue = calculateAQI(pollutants.get("pm25"));
                
                AqiData aqiData = new AqiData(
                    result.getCity(),
                    aqiValue,
                    pollutants.get("pm25"),
                    pollutants.get("pm10"),
                    pollutants.get("no2"),
                    pollutants.get("so2"),
                    pollutants.get("co"),
                    pollutants.get("o3")
                );
                
                aqiDataRepository.save(aqiData);
                System.out.println("AQI data saved for city: " + city + " with AQI: " + aqiValue);
            }
        } catch (Exception e) {
            System.err.println("Error fetching AQI data for city " + city + ": " + e.getMessage());
        }
    }
    
    private Integer calculateAQI(Double pm25) {
        if (pm25 == null) return 50; // Default moderate value
        
        if (pm25 <= 12.0) return (int) (pm25 * 50 / 12.0);
        else if (pm25 <= 35.5) return (int) (50 + (pm25 - 12.0) * 50 / 23.5);
        else if (pm25 <= 55.4) return (int) (100 + (pm25 - 35.5) * 50 / 19.9);
        else if (pm25 <= 150.4) return (int) (150 + (pm25 - 55.4) * 50 / 95.0);
        else if (pm25 <= 250.4) return (int) (200 + (pm25 - 150.4) * 100 / 100.0);
        else return (int) (300 + (pm25 - 250.4) * 100 / 149.6);
    }
}