package com.springup.domain.dto;

import com.springup.domain.model.Alarm;

import java.time.LocalTime;
import java.util.List;

public record AlarmResponseDto(
    Long id,
    String title,
    LocalTime alarmTime,
    boolean isEnabled,
    int repeatDaysMask,
    boolean wakeUpCheckEnabled,
    int wakeUpCheckDelayMinutes,
    List<MissionConfigDto> missions,
    boolean vibrate,
    int volumeLevel
) {
    public static AlarmResponseDto fromEntity(Alarm alarm) {
        List<MissionConfigDto> missionDtos = alarm.getMissions() == null ? List.of() :
                alarm.getMissions().stream()
                        .map(m -> new MissionConfigDto(
                                m.getStepOrder(),
                                m.getMinigame(),
                                m.getDifficulty(),
                                m.getRequiredCompletions(),
                                m.getTargetBarcodeHash()
                        ))
                        .toList();

        return new AlarmResponseDto(
            alarm.getId(),
            alarm.getTitle(),
            alarm.getAlarmTime(),
            alarm.isEnabled(),
            alarm.getRepeatDaysMask(),
            alarm.isWakeUpCheckEnabled(),
            alarm.getWakeUpCheckDelayMinutes(),
            missionDtos,
            alarm.isVibrate(),
            alarm.getVolumeLevel()
        );
    }
}