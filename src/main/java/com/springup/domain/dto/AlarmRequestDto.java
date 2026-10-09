package com.springup.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalTime;
import java.util.List;

public record AlarmRequestDto(
    @NotBlank(message = "Title is required")
    String title,

    @NotNull(message = "Alarm time is required")
    LocalTime alarmTime,

    boolean isEnabled,

    @Min(0) @Max(127)
    int repeatDaysMask,

    boolean wakeUpCheckEnabled,

    @Min(1) @Max(60)
    int wakeUpCheckDelayMinutes,

    @NotEmpty(message = "At least one mission must be configured")
    List<@Valid MissionConfigDto> missions,

    boolean vibrate,

    @Min(0) @Max(100)
    int volumeLevel
) {}