package com.zy.backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class SensorData {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private double temperature;
    private double pressure;
    private double flowRate;
    private double ph;
    private double turbidity;
    private double conductivity;

    // Getters and Setters
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public double getPressure() { return pressure; }
    public void setPressure(double pressure) { this.pressure = pressure; }
    public double getFlowRate() { return flowRate; }
    public void setFlowRate(double flowRate) { this.flowRate = flowRate; }
    public double getPh() { return ph; }
    public void setPh(double ph) { this.ph = ph; }
    public double getTurbidity() { return turbidity; }
    public void setTurbidity(double turbidity) { this.turbidity = turbidity; }
    public double getConductivity() { return conductivity; }
    public void setConductivity(double conductivity) { this.conductivity = conductivity; }
}