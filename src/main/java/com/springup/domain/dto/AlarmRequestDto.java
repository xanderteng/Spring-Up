package com.springup.domain.dto;

import com.springup.domain.enums.MinigameType;
import jakarta.validation.constraints.*;
import java.time.LocalTime;

public record AlarmRequestDto(
    @NotBlank(message = "Title is required")
    String title,

    @NotNull(message = "Alarm time is required")
    LocalTime alarmTime,

    boolean isEnabled,

    @Min(0) @Max(127)
    int repeatDaysMask,

    @NotNull(message = "Minigame type is required")
    MinigameType minigame,

    @Min(1) @Max(3)
    int minigameDifficulty,

    @Min(1) @Max(100)
    int requiredCompletions,

    String targetBarcodeHash,
    boolean vibrate,

    @Min(0) @Max(100)
    int volumeLevel
) {}