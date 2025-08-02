package com.air.airquality.util;

public class AQICalculator {
    
    public static int calculateAQI(Double pm25, Double pm10, Double no2, Double so2, Double co, Double o3) {
        int pm25AQI = calculatePM25AQI(pm25);
        int pm10AQI = calculatePM10AQI(pm10);
        int no2AQI = calculateNO2AQI(no2);
        int so2AQI = calculateSO2AQI(so2);
        int coAQI = calculateCOAQI(co);
        int o3AQI = calculateO3AQI(o3);
        
        // Return the maximum AQI value
        return Math.max(Math.max(Math.max(pm25AQI, pm10AQI), Math.max(no2AQI, so2AQI)), Math.max(coAQI, o3AQI));
    }
    
    private static int calculatePM25AQI(Double pm25) {
        if (pm25 == null) return 0;
        
        if (pm25 <= 12.0) return (int) Math.round(pm25 * 50 / 12.0);
        else if (pm25 <= 35.5) return (int) Math.round(50 + (pm25 - 12.0) * 50 / 23.5);
        else if (pm25 <= 55.4) return (int) Math.round(100 + (pm25 - 35.5) * 50 / 19.9);
        else if (pm25 <= 150.4) return (int) Math.round(150 + (pm25 - 55.4) * 50 / 95.0);
        else if (pm25 <= 250.4) return (int) Math.round(200 + (pm25 - 150.4) * 100 / 100.0);
        else if (pm25 <= 350.4) return (int) Math.round(300 + (pm25 - 250.4) * 100 / 100.0);
        else return 500;
    }
    
    private static int calculatePM10AQI(Double pm10) {
        if (pm10 == null) return 0;
        
        if (pm10 <= 54) return (int) Math.round(pm10 * 50 / 54);
        else if (pm10 <= 154) return (int) Math.round(50 + (pm10 - 54) * 50 / 100);
        else if (pm10 <= 254) return (int) Math.round(100 + (pm10 - 154) * 50 / 100);
        else if (pm10 <= 354) return (int) Math.round(150 + (pm10 - 254) * 50 / 100);
        else if (pm10 <= 424) return (int) Math.round(200 + (pm10 - 354) * 100 / 70);
        else if (pm10 <= 504) return (int) Math.round(300 + (pm10 - 424) * 100 / 80);
        else return 500;
    }
    
    private static int calculateNO2AQI(Double no2) {
        if (no2 == null) return 0;
        
        // Convert μg/m³ to ppb (approximately)
        double no2ppb = no2 * 0.53;
        
        if (no2ppb <= 53) return (int) Math.round(no2ppb * 50 / 53);
        else if (no2ppb <= 100) return (int) Math.round(50 + (no2ppb - 53) * 50 / 47);
        else if (no2ppb <= 360) return (int) Math.round(100 + (no2ppb - 100) * 50 / 260);
        else if (no2ppb <= 649) return (int) Math.round(150 + (no2ppb - 360) * 50 / 289);
        else if (no2ppb <= 1249) return (int) Math.round(200 + (no2ppb - 649) * 100 / 600);
        else return 500;
    }
    
    private static int calculateSO2AQI(Double so2) {
        if (so2 == null) return 0;
        
        // Convert μg/m³ to ppb (approximately)
        double so2ppb = so2 * 0.38;
        
        if (so2ppb <= 35) return (int) Math.round(so2ppb * 50 / 35);
        else if (so2ppb <= 75) return (int) Math.round(50 + (so2ppb - 35) * 50 / 40);
        else if (so2ppb <= 185) return (int) Math.round(100 + (so2ppb - 75) * 50 / 110);
        else if (so2ppb <= 304) return (int) Math.round(150 + (so2ppb - 185) * 50 / 119);
        else if (so2ppb <= 604) return (int) Math.round(200 + (so2ppb - 304) * 100 / 300);
        else return 500;
    }
    
    private static int calculateCOAQI(Double co) {
        if (co == null) return 0;
        
        // Convert mg/m³ to ppm (approximately)
        double coppm = co * 0.87;
        
        if (coppm <= 4.4) return (int) Math.round(coppm * 50 / 4.4);
        else if (coppm <= 9.4) return (int) Math.round(50 + (coppm - 4.4) * 50 / 5.0);
        else if (coppm <= 12.4) return (int) Math.round(100 + (coppm - 9.4) * 50 / 3.0);
        else if (coppm <= 15.4) return (int) Math.round(150 + (coppm - 12.4) * 50 / 3.0);
        else if (coppm <= 30.4) return (int) Math.round(200 + (coppm - 15.4) * 100 / 15.0);
        else return 500;
    }
    
    private static int calculateO3AQI(Double o3) {
        if (o3 == null) return 0;
        
        // Convert μg/m³ to ppb (approximately)
        double o3ppb = o3 * 0.51;
        
        if (o3ppb <= 54) return (int) Math.round(o3ppb * 50 / 54);
        else if (o3ppb <= 70) return (int) Math.round(50 + (o3ppb - 54) * 50 / 16);
        else if (o3ppb <= 85) return (int) Math.round(100 + (o3ppb - 70) * 50 / 15);
        else if (o3ppb <= 105) return (int) Math.round(150 + (o3ppb - 85) * 50 / 20);
        else if (o3ppb <= 200) return (int) Math.round(200 + (o3ppb - 105) * 100 / 95);
        else return 500;
    }
}
