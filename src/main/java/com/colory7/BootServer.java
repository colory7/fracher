package com.colory7;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.colory7")
public class BootServer {
    public static void main(String[] args) {
        SpringApplication.run(BootServer.class);
    }
}