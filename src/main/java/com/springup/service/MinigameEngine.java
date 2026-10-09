package com.springup.service;

import com.springup.domain.dto.MinigameDtos.*;
import com.springup.domain.enums.MinigameType;
import com.springup.domain.model.Alarm;
import com.springup.domain.model.DailyClear;
import com.springup.domain.model.MissionConfig;
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

    public ChallengeResponse generateChallenge(MinigameType type, int difficulty) {
        return switch (type) {
            case MATH -> generateMathChallenge(difficulty);
            case GRID_MEMORY -> generateGridMemoryChallenge(difficulty);
            case POWER_SHAKE -> new ChallengeResponse(type, difficulty, "Shake threshold: " + (difficulty * 20), null);
            case BARCODE_SCAN -> new ChallengeResponse(type, difficulty, "Scan registered barcode", null);
            case NONE -> new ChallengeResponse(type, difficulty, "Simple Dismiss", null);
        };
    }

    @Transactional
    public VerificationResponse verifyAndDismiss(VerificationRequest request) {
        Alarm alarm = alarmRepository.findById(request.alarmId())
                .orElseThrow(() -> new ResourceNotFoundException("Alarm not found: " + request.alarmId()));

        // Resolve matching mission configuration from the alarm's gauntlet
        MissionConfig missionConfig = alarm.getMissions().stream()
                .filter(m -> m.getMinigame() == request.minigameType())
                .findFirst()
                .orElse(null);

        int difficulty = missionConfig != null ? missionConfig.getDifficulty() : 1;
        String targetBarcodeHash = missionConfig != null ? missionConfig.getTargetBarcodeHash() : null;

        boolean isValid = switch (request.minigameType()) {
            case MATH -> verifyMath(request.submittedSolution(), difficulty);
            case GRID_MEMORY -> verifyGrid(request.submittedSequence(), difficulty);
            case BARCODE_SCAN -> verifyBarcode(request.submittedSolution(), targetBarcodeHash);
            case POWER_SHAKE -> verifyShake(request.submittedSolution(), difficulty);
            case NONE -> true;
        };

        if (!isValid) {
            throw new InvalidMinigameSolutionException("Minigame solution incorrect. Alarm keeps ringing!");
        }

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

    private ChallengeResponse generateMathChallenge(int difficulty) {
        int a = random.nextInt(10 * difficulty) + 5;
        int b = random.nextInt(10 * difficulty) + 2;
        String payload = a + " + " + b;
        return new ChallengeResponse(MinigameType.MATH, difficulty, payload, null);
    }

    private boolean verifyMath(String answer, int difficulty) {
        try {
            int numericAnswer = Integer.parseInt(answer.trim());
            return numericAnswer > 0;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }

    private ChallengeResponse generateGridMemoryChallenge(int difficulty) {
        int length = 2 + (difficulty * 2);
        List<Integer> sequence = random.ints(length, 1, 10).boxed().toList();
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
            int threshold = difficulty * 20;
            return shakes >= threshold;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }

    private int calculateStreak(Long alarmId) {
        long recentClears = dailyClearRepository.countSuccessfulClearsSince(
                alarmId, OffsetDateTime.now().minusDays(7));
        return (int) recentClears;
    }
}