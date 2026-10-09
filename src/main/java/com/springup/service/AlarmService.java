package com.springup.service;

import com.springup.domain.dto.AlarmRequestDto;
import com.springup.domain.dto.AlarmResponseDto;

import java.util.List;

public interface AlarmService {
    List<AlarmResponseDto> findAllAlarms();
    AlarmResponseDto findAlarmById(Long id);
    AlarmResponseDto createAlarm(AlarmRequestDto dto);
    AlarmResponseDto updateAlarm(Long id, AlarmRequestDto dto);
    AlarmResponseDto toggleAlarm(Long id, boolean enabled);
    void deleteAlarm(Long id);
}