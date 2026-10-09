package com.springup.service.impl;

import com.springup.domain.dto.AlarmRequestDto;
import com.springup.domain.dto.AlarmResponseDto;
import com.springup.domain.model.Alarm;
import com.springup.domain.model.MissionConfig;
import com.springup.exception.ResourceNotFoundException;
import com.springup.repository.AlarmRepository;
import com.springup.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
        List<MissionConfig> missions = new ArrayList<>();
        if (dto.missions() != null) {
            for (var mDto : dto.missions()) {
                missions.add(MissionConfig.builder()
                        .stepOrder(mDto.stepOrder())
                        .minigame(mDto.minigame())
                        .difficulty(mDto.difficulty())
                        .requiredCompletions(mDto.requiredCompletions())
                        .targetBarcodeHash(mDto.targetBarcodeHash())
                        .build());
            }
        }

        Alarm alarm = Alarm.builder()
                .title(dto.title())
                .alarmTime(dto.alarmTime())
                .isEnabled(dto.isEnabled())
                .repeatDaysMask(dto.repeatDaysMask())
                .wakeUpCheckEnabled(dto.wakeUpCheckEnabled())
                .wakeUpCheckDelayMinutes(dto.wakeUpCheckDelayMinutes())
                .missions(missions)
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
        alarm.setWakeUpCheckEnabled(dto.wakeUpCheckEnabled());
        alarm.setWakeUpCheckDelayMinutes(dto.wakeUpCheckDelayMinutes());
        alarm.setVibrate(dto.vibrate());
        alarm.setVolumeLevel(dto.volumeLevel());

        alarm.getMissions().clear();
        if (dto.missions() != null) {
            for (var mDto : dto.missions()) {
                alarm.getMissions().add(MissionConfig.builder()
                        .stepOrder(mDto.stepOrder())
                        .minigame(mDto.minigame())
                        .difficulty(mDto.difficulty())
                        .requiredCompletions(mDto.requiredCompletions())
                        .targetBarcodeHash(mDto.targetBarcodeHash())
                        .build());
            }
        }

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