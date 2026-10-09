package com.springup.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

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

    // Bitmask for days: Mon=1, Tue=2, Wed=4, Thu=8, Fri=16, Sat=32, Sun=64 (31 = Weekdays)
    @Column(nullable = false)
    @Builder.Default
    private int repeatDaysMask = 31;

    // --- Wake Up Check Watchdog Settings ---
    @Column(nullable = false)
    @Builder.Default
    private boolean wakeUpCheckEnabled = true;

    @Column(nullable = false)
    @Builder.Default
    private int wakeUpCheckDelayMinutes = 5;

    // --- Ordered Mission Chain ---
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "alarm_missions", joinColumns = @JoinColumn(name = "alarm_id"))
    @OrderBy("stepOrder ASC")
    @Builder.Default
    private List<MissionConfig> missions = new ArrayList<>();

    @Builder.Default
    private boolean vibrate = true;

    @Builder.Default
    private int volumeLevel = 100;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}