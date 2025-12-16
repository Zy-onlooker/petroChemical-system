package com.zy.backend.model;

public class MonitoringData {
    private String sensorId;
    private SensorData data;
    private String status;

    // Getters and Setters
    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }
    public SensorData getData() { return data; }
    public void setData(SensorData data) { this.data = data; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}