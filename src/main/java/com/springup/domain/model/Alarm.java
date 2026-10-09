package com.springup.domain.model;

import com.springup.domain.enums.MinigameType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "alarms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String title;

    @NotNull
    @Column(nullable = false)
    private LocalTime alarmTime;

    @Column(nullable = false)
    @Builder.Default
    private boolean isEnabled = true;

    // Bitmask for days: Mon=1, Tue=2, Wed=4, Thu=8, Fri=16, Sat=32, Sun=64
    @Column(nullable = false)
    @Builder.Default
    private int repeatDaysMask = 31; // Default: Weekdays

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MinigameType minigame = MinigameType.MATH;

    @Column(nullable = false)
    @Builder.Default
    private int minigameDifficulty = 1; // 1 = Easy, 2 = Medium, 3 = Hard

    @Column(nullable = false)
    @Builder.Default
    private int requiredCompletions = 3; // e.g., 3 equations, 40 shakes, 3 grid patterns

    @Column(length = 255)
    private String targetBarcodeHash;

    @Builder.Default
    private boolean vibrate = true;

    @Builder.Default
    private int volumeLevel = 100;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
