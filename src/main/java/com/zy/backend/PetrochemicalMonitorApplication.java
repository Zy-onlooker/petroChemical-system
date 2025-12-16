package com.zy.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PetrochemicalMonitorApplication {
    public static void main(String[] args) {
        SpringApplication.run(PetrochemicalMonitorApplication.class, args);
        System.out.println("石化工业智能监测仿真系统后端启动在 :8080");
    }
}