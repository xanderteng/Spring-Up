package com.springup.service;

import com.springup.domain.dto.MinigameDtos.*;
import com.springup.domain.enums.MinigameType;
import com.springup.domain.model.Alarm;
import com.springup.domain.model.DailyClear;
import com.springup.exception.InvalidMinigameSolutionException;
import com.springup.exception.ResourceNotFoundException;
import com.springup.repository.AlarmRepository;
import com.springup.repository.DailyClearRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MinigameEngine {

    private final AlarmRepository alarmRepository;
    private final DailyClearRepository dailyClearRepository;
    private final Random random = new Random();

    // 1. Generate challenge based on minigame and difficulty
    public ChallengeResponse generateChallenge(MinigameType type, int difficulty) {
        return switch (type) {
            case MATH -> generateMathChallenge(difficulty);
            case GRID_MEMORY -> generateGridMemoryChallenge(difficulty);
            case POWER_SHAKE -> new ChallengeResponse(type, difficulty, "Shake threshold: " + (difficulty * 20), null);
            case BARCODE_SCAN -> new ChallengeResponse(type, difficulty, "Scan registered barcode", null);
            case NONE -> new ChallengeResponse(type, difficulty, "Simple Dismiss", null);
        };
    }

    // 2. Verify submission and award streak
    @Transactional
    public VerificationResponse verifyAndDismiss(VerificationRequest request) {
        Alarm alarm = alarmRepository.findById(request.alarmId())
                .orElseThrow(() -> new ResourceNotFoundException("Alarm not found: " + request.alarmId()));

        boolean isValid = switch (request.minigameType()) {
            case MATH -> verifyMath(request.submittedSolution(), alarm.getMinigameDifficulty());
            case GRID_MEMORY -> verifyGrid(request.submittedSequence(), alarm.getMinigameDifficulty());
            case BARCODE_SCAN -> verifyBarcode(request.submittedSolution(), alarm.getTargetBarcodeHash());
            case POWER_SHAKE -> verifyShake(request.submittedSolution(), alarm.getMinigameDifficulty());
            case NONE -> true;
        };

        if (!isValid) {
            throw new InvalidMinigameSolutionException("Minigame solution incorrect. Alarm keeps ringing!");
        }

        // Record successful daily clear
        DailyClear clear = DailyClear.builder()
                .alarm(alarm)
                .minigameCompleted(request.minigameType())
                .secondsToClear(request.secondsTaken())
                .snoozeCount(request.snoozeCount())
                .isSuccessful(true)
                .build();
        dailyClearRepository.save(clear);

        int currentStreak = calculateStreak(alarm.getId());

        return new VerificationResponse(true, currentStreak, "STAGE CLEARED! Alarm dismissed.");
    }

    // --- Minigame Internal Solvers ---

    private ChallengeResponse generateMathChallenge(int difficulty) {
        int a = random.nextInt(10 * difficulty) + 5;
        int b = random.nextInt(10 * difficulty) + 2;
        String payload = a + " + " + b;
        return new ChallengeResponse(MinigameType.MATH, difficulty, payload, null);
    }

    private boolean verifyMath(String answer, int difficulty) {
        try {
            int numericAnswer = Integer.parseInt(answer.trim());
            // In a production setup, compute from the cached session or token.
            return numericAnswer > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private ChallengeResponse generateGridMemoryChallenge(int difficulty) {
        // e.g., Difficulty 1 = 3 tiles, Difficulty 2 = 5 tiles, Difficulty 3 = 7 tiles
        int length = 2 + (difficulty * 2);
        List<Integer> sequence = random.ints(length, 1, 10).boxed().toList(); // 1 to 9 (3x3 grid)
        return new ChallengeResponse(MinigameType.GRID_MEMORY, difficulty, "Repeat pattern", sequence);
    }

    private boolean verifyGrid(List<Integer> submittedSequence, int difficulty) {
        int expectedLength = 2 + (difficulty * 2);
        return submittedSequence != null && submittedSequence.size() == expectedLength;
    }

    private boolean verifyBarcode(String rawBarcode, String targetHash) {
        if (rawBarcode == null || targetHash == null) return false;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawBarcode.getBytes());
            String hex = HexFormat.of().formatHex(hash);
            return hex.equalsIgnoreCase(targetHash);
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }

    private boolean verifyShake(String shakeCountStr, int difficulty) {
        try {
            int shakes = Integer.parseInt(shakeCountStr.trim());
            int threshold = difficulty * 20; // 20, 40, or 60 shakes
            return shakes >= threshold;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private int calculateStreak(Long alarmId) {
        // Calculate consecutive days cleared
        long recentClears = dailyClearRepository.countSuccessfulClearsSince(
                alarmId, OffsetDateTime.now().minusDays(7));
        return (int) recentClears;
    }
}