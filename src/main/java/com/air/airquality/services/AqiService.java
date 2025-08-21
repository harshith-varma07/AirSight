package com.air.airquality.services;

import com.air.airquality.dto.AqiResponse;
import com.air.airquality.model.AqiData;
import com.air.airquality.repository.AqiDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AqiService {
    
    private static final Logger logger = LoggerFactory.getLogger(AqiService.class);
    
    @Autowired
    private AqiDataRepository aqiDataRepository;
    
    public AqiResponse getCurrentAqi(String city) {
        Optional<AqiData> latestData = aqiDataRepository.findTopByCityOrderByTimestampDesc(city);
        
        if (latestData.isPresent()) {
            return convertToResponse(latestData.get());
        }
        
        throw new RuntimeException("No AQI data found for city: " + city);
    }
    
    public List<AqiResponse> getHistoricalData(String city, LocalDateTime startDate, LocalDateTime endDate) {
        List<AqiData> historicalData = aqiDataRepository.findByCityAndTimestampBetween(city, startDate, endDate);
        
        return historicalData.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<String> getAvailableCities() {
        return aqiDataRepository.findDistinctCities();
    }
    
    // Get latest data for all cities (optimized for dashboard)
    public List<AqiResponse> getAllCitiesLatestData() {
        return aqiDataRepository.findLatestDataForAllCities()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // Cleanup old data (maintenance method)
    public void cleanupOldData(int daysToKeep) {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
            aqiDataRepository.deleteOldData(cutoffDate);
            logger.info("Cleaned up AQI data older than {} days", daysToKeep);
        } catch (Exception e) {
            logger.error("Error cleaning up old data: {}", e.getMessage());
        }
    }
    
    private AqiResponse convertToResponse(AqiData data) {
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
    
    // Health check methods for deployment readiness
    public long getTotalRecords() {
        try {
            return aqiDataRepository.count();
        } catch (Exception e) {
            logger.error("Error getting total records: {}", e.getMessage());
            return -1;
        }
    }
    
    public boolean isDatabaseReady() {
        try {
            aqiDataRepository.count();
            return true;
        } catch (Exception e) {
            logger.error("Database not ready: {}", e.getMessage());
            return false;
        }
    }
}