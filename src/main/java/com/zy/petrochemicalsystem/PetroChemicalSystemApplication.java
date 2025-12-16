package com.zy.petrochemicalsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.zy.petrochemicalsystem", "com.zy.backend"})
public class PetroChemicalSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(PetroChemicalSystemApplication.class, args);
        System.out.println("石化工业智能监测仿真系统后端启动在 :8080");
    }

}
