package com.air.airquality.repository;
import com.air.airquality.model.AirQualityData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface AirQualityRepository extends JpaRepository<AirQualityData, Long> {
    List<AirQualityData> findByLocation(String location);
    List<AirQualityData> findTop10ByLocationOrderByTimestampDesc(String location);
    List<AirQualityData> findTop10ByOrderByTimestampDesc();
}