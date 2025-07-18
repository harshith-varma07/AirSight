package com.air.airquality.repository;

import com.air.airquality.model.AirQualityData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AirQualityRepository extends JpaRepository<AirQualityData, Long> {
    List<AirQualityData> findByCity(String city);
    List<AirQualityData> findByCityAndParameter(String city, String parameter);
    List<AirQualityData> findTop10ByCityAndParameterOrderByTimestampDesc(String city, String parameter);
}