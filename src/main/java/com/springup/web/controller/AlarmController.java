package com.springup.web.controller;

import com.springup.domain.dto.AlarmRequestDto;
import com.springup.domain.dto.AlarmResponseDto;
import com.springup.service.AlarmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alarms")
@RequiredArgsConstructor

public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping
    public ResponseEntity<List<AlarmResponseDto>> getAllAlarms() {
        return ResponseEntity.ok(alarmService.findAllAlarms());
    }

    @PostMapping
    public ResponseEntity<AlarmResponseDto> createAlarm(@Valid @RequestBody AlarmRequestDto dto) {
        AlarmResponseDto created = alarmService.createAlarm(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<AlarmResponseDto> toggleAlarm(
            @PathVariable Long id, 
            @RequestParam boolean enabled) {
        return ResponseEntity.ok(alarmService.toggleAlarm(id, enabled));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlarm(@PathVariable Long id) {
        alarmService.deleteAlarm(id);
        return ResponseEntity.noContent().build();
    }
}