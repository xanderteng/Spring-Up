package com.springup.service;

import com.springup.domain.dto.MinigameDtos.*;
import com.springup.domain.enums.MinigameType;
import com.springup.domain.model.Alarm;
import com.springup.domain.model.MissionConfig;
import com.springup.exception.InvalidMinigameSolutionException;
import com.springup.repository.AlarmRepository;
import com.springup.repository.DailyClearRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MinigameEngineTest {

    @Mock
    private AlarmRepository alarmRepository;

    @Mock
    private DailyClearRepository dailyClearRepository;

    @InjectMocks
    private MinigameEngine minigameEngine;

    private Alarm sampleAlarm;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {
        byte[] hash = MessageDigest.getInstance("SHA-256").digest("123456789".getBytes());
        String barcodeHash = HexFormat.of().formatHex(hash);

        List<MissionConfig> missions = List.of(
                MissionConfig.builder()
                        .stepOrder(1)
                        .minigame(MinigameType.MATH)
                        .difficulty(2)
                        .requiredCompletions(3)
                        .build(),
                MissionConfig.builder()
                        .stepOrder(2)
                        .minigame(MinigameType.BARCODE_SCAN)
                        .difficulty(1)
                        .requiredCompletions(1)
                        .targetBarcodeHash(barcodeHash)
                        .build()
        );

        sampleAlarm = Alarm.builder()
                .id(1L)
                .title("Multi-Stage Test Alarm")
                .alarmTime(LocalTime.of(7, 0))
                .wakeUpCheckEnabled(true)
                .wakeUpCheckDelayMinutes(5)
                .missions(missions)
                .build();
    }

    @Test
    @DisplayName("Challenge generator produces valid 3x3 Grid Memory sequence")
    void generateChallenge_GridMemory_ReturnsCorrectSequenceLength() {
        ChallengeResponse challenge = minigameEngine.generateChallenge(MinigameType.GRID_MEMORY, 2);

        assertNotNull(challenge);
        assertEquals(MinigameType.GRID_MEMORY, challenge.type());
        assertNotNull(challenge.memorySequence());
        assertEquals(6, challenge.memorySequence().size());
        assertTrue(challenge.memorySequence().stream().allMatch(n -> n >= 1 && n <= 9));
    }

    @Test
    @DisplayName("Barcode verification succeeds when submitted barcode matches target SHA-256 hash in mission")
    void verifyAndDismiss_BarcodeScan_SucceedsOnMatchingHash() {
        when(alarmRepository.findById(1L)).thenReturn(Optional.of(sampleAlarm));
        when(dailyClearRepository.countSuccessfulClearsSince(eq(1L), any())).thenReturn(4L);

        VerificationRequest request = new VerificationRequest(
                1L,
                MinigameType.BARCODE_SCAN,
                "123456789",
                null,
                15,
                0
        );

        VerificationResponse response = minigameEngine.verifyAndDismiss(request);

        assertTrue(response.isDismissed());
        assertEquals(4, response.currentStreak());
        verify(dailyClearRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Barcode verification throws InvalidMinigameSolutionException on wrong barcode")
    void verifyAndDismiss_BarcodeScan_ThrowsOnWrongBarcode() {
        when(alarmRepository.findById(1L)).thenReturn(Optional.of(sampleAlarm));

        VerificationRequest request = new VerificationRequest(
                1L,
                MinigameType.BARCODE_SCAN,
                "WRONG_BARCODE",
                null,
                10,
                0
        );

        assertThrows(InvalidMinigameSolutionException.class, () -> {
            minigameEngine.verifyAndDismiss(request);
        });

        verify(dailyClearRepository, never()).save(any());
    }
}