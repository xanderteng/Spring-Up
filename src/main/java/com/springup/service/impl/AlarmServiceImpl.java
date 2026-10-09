package com.springup.service.impl;

import com.springup.domain.dto.AlarmRequestDto;
import com.springup.domain.dto.AlarmResponseDto;
import com.springup.domain.model.Alarm;
import com.springup.exception.ResourceNotFoundException;
import com.springup.repository.AlarmRepository;
import com.springup.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlarmServiceImpl implements AlarmService {

    private final AlarmRepository alarmRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AlarmResponseDto> findAllAlarms() {
        return alarmRepository.findAll()
                .stream()
                .map(AlarmResponseDto::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AlarmResponseDto findAlarmById(Long id) {
        return alarmRepository.findById(id)
                .map(AlarmResponseDto::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Alarm not found with ID: " + id));
    }

    @Override
    @Transactional
    public AlarmResponseDto createAlarm(AlarmRequestDto dto) {
        Alarm alarm = Alarm.builder()
                .title(dto.title())
                .alarmTime(dto.alarmTime())
                .isEnabled(dto.isEnabled())
                .repeatDaysMask(dto.repeatDaysMask())
                .minigame(dto.minigame())
                .minigameDifficulty(dto.minigameDifficulty())
                .requiredCompletions(dto.requiredCompletions())
                .targetBarcodeHash(dto.targetBarcodeHash())
                .vibrate(dto.vibrate())
                .volumeLevel(dto.volumeLevel())
                .build();

        Alarm saved = alarmRepository.save(alarm);
        return AlarmResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public AlarmResponseDto updateAlarm(Long id, AlarmRequestDto dto) {
        Alarm alarm = alarmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alarm not found with ID: " + id));

        alarm.setTitle(dto.title());
        alarm.setAlarmTime(dto.alarmTime());
        alarm.setEnabled(dto.isEnabled());
        alarm.setRepeatDaysMask(dto.repeatDaysMask());
        alarm.setMinigame(dto.minigame());
        alarm.setMinigameDifficulty(dto.minigameDifficulty());
        alarm.setRequiredCompletions(dto.requiredCompletions());
        alarm.setTargetBarcodeHash(dto.targetBarcodeHash());
        alarm.setVibrate(dto.vibrate());
        alarm.setVolumeLevel(dto.volumeLevel());

        return AlarmResponseDto.fromEntity(alarm);
    }

    @Override
    @Transactional
    public AlarmResponseDto toggleAlarm(Long id, boolean enabled) {
        Alarm alarm = alarmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alarm not found with ID: " + id));
        alarm.setEnabled(enabled);
        return AlarmResponseDto.fromEntity(alarm);
    }

    @Override
    @Transactional
    public void deleteAlarm(Long id) {
        if (!alarmRepository.existsById(id)) {
            throw new ResourceNotFoundException("Alarm not found with ID: " + id);
        }
        alarmRepository.deleteById(id);
    }
}