package com.springup.domain.dto;

import com.springup.domain.enums.MinigameType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MissionConfigDto(
    @Min(1)
    int stepOrder,

    @NotNull(message = "Minigame type is required")
    MinigameType minigame,

    @Min(1) @Max(3)
    int difficulty,

    @Min(1) @Max(100)
    int requiredCompletions,

    String targetBarcodeHash
) {}