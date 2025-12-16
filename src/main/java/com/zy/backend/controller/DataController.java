package com.zy.backend.controller;

import com.zy.backend.model.BlastSensorData;
import com.zy.backend.model.MonitoringData;
import com.zy.backend.model.SensorData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api")
public class DataController {
    private final Random random = new Random();

    @GetMapping("/data")
    public Map<String, MonitoringData> getCurrentData() {
        String[] sensors = {"reactor_1", "pipeline_a", "tank_main", "pump_primary"};
        Map<String, MonitoringData> dataMap = new HashMap<>();

        for (String sensorId : sensors) {
            MonitoringData data = new MonitoringData();
            data.setSensorId(sensorId);
            data.setData(generateRandomData());
            data.setStatus("normal");
            dataMap.put(sensorId, data);
        }
        return dataMap;
    }

    @GetMapping("/blast-data")
    public Map<String, BlastSensorData> getBlastData() {
        String[] blastZones = {"blast_zone_1", "blast_zone_2", "blast_zone_3", "blast_zone_4"};
        Map<String, BlastSensorData> dataMap = new HashMap<>();

        for (String zoneId : blastZones) {
            dataMap.put(zoneId, generateBlastData());
        }
        return dataMap;
    }

    // 生成随机传感器数据
    private SensorData generateRandomData() {
        SensorData data = new SensorData();
        data.setTimestamp(LocalDateTime.now());
        data.setTemperature(20 + random.nextDouble() * 30);    // 20-50°C
        data.setPressure(1 + random.nextDouble() * 4);         // 1-5 bar
        data.setFlowRate(10 + random.nextDouble() * 90);       // 10-100 L/min
        data.setPh(6 + random.nextDouble() * 2);               // 6-8 pH
        data.setTurbidity(random.nextDouble() * 10);           // 0-10 NTU
        data.setConductivity(100 + random.nextDouble() * 900); // 100-1000 μS/cm
        return data;
    }

    // 生成随机爆破区域数据
    private BlastSensorData generateBlastData() {
        BlastSensorData data = new BlastSensorData();
        data.setTimestamp(LocalDateTime.now());
        data.setVibrationLevel(random.nextDouble() * 100);      // 0-100 mm/s
        data.setNoiseLevel(60 + random.nextDouble() * 40);      // 60-100 dB
        data.setAirQuality(50 + random.nextDouble() * 50);      // 50-100 AQI
        data.setPersonnelCount(random.nextInt(20));             // 0-20 people
        data.setSafetyStatus("safe");
        data.setExplosiveGas(random.nextDouble() * 10);         // 0-10 ppm
        data.setDistanceToBlast(100 + random.nextDouble() * 900); // 100-1000 meters
        return data;
    }
}