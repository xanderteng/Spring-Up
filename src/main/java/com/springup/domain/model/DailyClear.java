package com.springup.domain.model;

import com.springup.domain.enums.MinigameType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "daily_clears")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyClear {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alarm_id", nullable = false)
    private Alarm alarm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MinigameType minigameCompleted;

    @Column(nullable = false)
    private int secondsToClear;

    @Column(nullable = false)
    private int snoozeCount;

    @Column(nullable = false)
    private boolean isSuccessful;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime clearedAt;
}
