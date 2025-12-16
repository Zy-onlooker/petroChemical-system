package com.zy.backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class BlastSensorData {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private double vibrationLevel;
    private double noiseLevel;
    private double airQuality;
    private int personnelCount;
    private String safetyStatus;
    private double explosiveGas;
    private double distanceToBlast;

    // Getters and Setters
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public double getVibrationLevel() { return vibrationLevel; }
    public void setVibrationLevel(double vibrationLevel) { this.vibrationLevel = vibrationLevel; }
    public double getNoiseLevel() { return noiseLevel; }
    public void setNoiseLevel(double noiseLevel) { this.noiseLevel = noiseLevel; }
    public double getAirQuality() { return airQuality; }
    public void setAirQuality(double airQuality) { this.airQuality = airQuality; }
    public int getPersonnelCount() { return personnelCount; }
    public void setPersonnelCount(int personnelCount) { this.personnelCount = personnelCount; }
    public String getSafetyStatus() { return safetyStatus; }
    public void setSafetyStatus(String safetyStatus) { this.safetyStatus = safetyStatus; }
    public double getExplosiveGas() { return explosiveGas; }
    public void setExplosiveGas(double explosiveGas) { this.explosiveGas = explosiveGas; }
    public double getDistanceToBlast() { return distanceToBlast; }
    public void setDistanceToBlast(double distanceToBlast) { this.distanceToBlast = distanceToBlast; }
}