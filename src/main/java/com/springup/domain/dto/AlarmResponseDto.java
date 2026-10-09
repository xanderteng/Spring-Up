package com.springup.domain.dto;

import com.springup.domain.enums.MinigameType;
import com.springup.domain.model.Alarm;
import java.time.LocalTime;

public record AlarmResponseDto(
    Long id,
    String title,
    LocalTime alarmTime,
    boolean isEnabled,
    int repeatDaysMask,
    MinigameType minigame,
    int minigameDifficulty,
    int requiredCompletions,
    boolean vibrate,
    int volumeLevel
) {
    public static AlarmResponseDto fromEntity(Alarm alarm) {
        return new AlarmResponseDto(
            alarm.getId(),
            alarm.getTitle(),
            alarm.getAlarmTime(),
            alarm.isEnabled(),
            alarm.getRepeatDaysMask(),
            alarm.getMinigame(),
            alarm.getMinigameDifficulty(),
            alarm.getRequiredCompletions(),
            alarm.isVibrate(),
            alarm.getVolumeLevel()
        );
    }
}