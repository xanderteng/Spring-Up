package com.springup.repository;

import com.springup.domain.model.DailyClear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface DailyClearRepository extends JpaRepository<DailyClear, Long> {
    
    // Fetch user history ordered by completion time for streak analytics
    List<DailyClear> findByAlarmIdOrderByClearedAtDesc(Long alarmId);

    // Count successful clears in a specific time range (e.g., today)
    @Query("SELECT COUNT(d) FROM DailyClear d WHERE d.alarm.id = :alarmId AND d.isSuccessful = true AND d.clearedAt >= :since")
    long countSuccessfulClearsSince(@Param("alarmId") Long alarmId, @Param("since") OffsetDateTime since);
}