package com.springup.domain.model;

import com.springup.domain.enums.MinigameType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MissionConfig {

    @Column(nullable = false)
    private int stepOrder; // 1, 2, 3...

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MinigameType minigame;

    @Column(nullable = false)
    private int difficulty; // 1 = Easy, 2 = Medium, 3 = Hard

    @Column(nullable = false)
    private int requiredCompletions; // e.g. 3 math equations, 40 shakes

    @Column(length = 255)
    private String targetBarcodeHash; // SHA-256 target hash if minigame is BARCODE_SCAN
}