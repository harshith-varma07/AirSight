package com.air.airquality.services;

import com.air.airquality.dto.AqiResponse;
import com.air.airquality.model.AqiData;
import com.air.airquality.repository.AqiDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AqiService {
    
    @Autowired
    private AqiDataRepository aqiDataRepository;
    
    public AqiResponse getCurrentAqi(String city) {
        Optional<AqiData> latestData = aqiDataRepository.findLatestByCityNative(city);
        
        if (latestData.isPresent()) {
            AqiData data = latestData.get();
            return new AqiResponse(
                data.getCity(),
                data.getAqiValue(),
                data.getPm25(),
                data.getPm10(),
                data.getNo2(),
                data.getSo2(),
                data.getCo(),
                data.getO3(),
                data.getTimestamp()
            );
        }
        
        throw new RuntimeException("No AQI data found for city: " + city);
    }
    
    public List<AqiResponse> getHistoricalData(String city, LocalDateTime startDate, LocalDateTime endDate) {
        List<AqiData> historicalData = aqiDataRepository.findByCityAndTimestampBetween(city, startDate, endDate);
        
        return historicalData.stream()
                .map(data -> new AqiResponse(
                    data.getCity(),
                    data.getAqiValue(),
                    data.getPm25(),
                    data.getPm10(),
                    data.getNo2(),
                    data.getSo2(),
                    data.getCo(),
                    data.getO3(),
                    data.getTimestamp()
                ))
                .collect(Collectors.toList());
    }
    
    public List<String> getAvailableCities() {
        return aqiDataRepository.findDistinctCities();
    }
}