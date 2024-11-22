package com.sogonsogon.neighclova.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        // ALB를 통한 ec2 HTTP 통신 체크 용도
        return ResponseEntity.ok("Healthy");
    }
}
