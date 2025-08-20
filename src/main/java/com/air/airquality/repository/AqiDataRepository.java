package com.air.airquality.repository;

import com.air.airquality.model.AqiData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AqiDataRepository extends JpaRepository<AqiData, Long> {
    
    @Query("SELECT a FROM AqiData a WHERE a.city = :city ORDER BY a.timestamp DESC")
    List<AqiData> findByCityOrderByTimestampDesc(@Param("city") String city);
    
    @Query(value = "SELECT * FROM aqi_data a WHERE a.city = :city ORDER BY a.timestamp DESC LIMIT 1", nativeQuery = true)
    Optional<AqiData> findLatestByCityNative(@Param("city") String city);
    
    @Query("SELECT a FROM AqiData a WHERE a.city = :city AND a.timestamp BETWEEN :startDate AND :endDate ORDER BY a.timestamp")
    List<AqiData> findByCityAndTimestampBetween(@Param("city") String city, 
                                               @Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT DISTINCT a.city FROM AqiData a")
    List<String> findDistinctCities();
}