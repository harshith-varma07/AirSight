package com.air.airquality.repository;
import com.air.airquality.model.AirQualityData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface AirQualityRepository extends JpaRepository<AirQualityData, Long> {
    List<AirQualityData> findByCity(String city);
    List<AirQualityData> findTop10ByCityOrderByTimestampDesc(String city);
    List<AirQualityData> findTop10ByOrderByTimestampDesc();
    List<AirQualityData> findTop30ByCityOrderByTimestampDesc(String city);
}