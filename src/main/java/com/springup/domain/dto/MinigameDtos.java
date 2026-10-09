package com.springup.domain.dto;

import com.springup.domain.enums.MinigameType;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class MinigameDtos {

    // Challenge dispatched to client
    public record ChallengeResponse(
            MinigameType type,
            int difficulty,
            String challengePayload,      // e.g., "34 * 12 + 15" for Math
            List<Integer> memorySequence  // e.g., [2, 5, 8, 1] for 3x3 Grid Memory
    ) {}

    // Submission from client to dismiss alarm
    public record VerificationRequest(
            @NotNull Long alarmId,
            @NotNull MinigameType minigameType,
            String submittedSolution,     // Math answer, barcode string, or shake count
            List<Integer> submittedSequence, // Grid memory sequence clicked by user
            int secondsTaken,
            int snoozeCount
    ) {}

    // Result returned to client
    public record VerificationResponse(
            boolean isDismissed,
            int currentStreak,
            String message
    ) {}
}