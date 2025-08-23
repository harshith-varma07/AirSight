package com.air.airquality.repository;

import com.air.airquality.model.AqiData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AqiDataRepository extends JpaRepository<AqiData, Long> {
    
    // Optimized query using index on city and timestamp
    Optional<AqiData> findTopByCityOrderByTimestampDesc(String city);
    
    // Efficient query for historical data with pagination support
    @Query("SELECT a FROM AqiData a WHERE a.city = :city AND a.timestamp BETWEEN :startDate AND :endDate ORDER BY a.timestamp DESC")
    List<AqiData> findByCityAndTimestampBetween(@Param("city") String city, 
                                               @Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);
    
    // Optimized distinct cities query
    @Query("SELECT DISTINCT a.city FROM AqiData a ORDER BY a.city")
    List<String> findDistinctCities();
    
    // Check if city exists (for optimization)
    boolean existsByCity(String city);
    
    // Cleanup old data (for maintenance)
    @Modifying
    @Query("DELETE FROM AqiData a WHERE a.timestamp < :cutoffDate")
    void deleteOldData(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // Get latest data for all cities (for dashboard)
    @Query("""
        SELECT a FROM AqiData a WHERE a.timestamp = 
        (SELECT MAX(a2.timestamp) FROM AqiData a2 WHERE a2.city = a.city)
        ORDER BY a.city
        """)
    List<AqiData> findLatestDataForAllCities();
}