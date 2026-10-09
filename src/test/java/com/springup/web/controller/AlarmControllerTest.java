package com.springup.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springup.domain.dto.AlarmRequestDto;
import com.springup.domain.dto.AlarmResponseDto;
import com.springup.domain.dto.MissionConfigDto;
import com.springup.domain.enums.MinigameType;
import com.springup.service.AlarmService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlarmController.class)
class AlarmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AlarmService alarmService;

    @Test
    @DisplayName("POST /api/v1/alarms returns 201 when valid payload with missions is provided")
    void createAlarm_ValidPayload_Returns201() throws Exception {
        List<MissionConfigDto> missions = List.of(
                new MissionConfigDto(1, MinigameType.MATH, 2, 3, null),
                new MissionConfigDto(2, MinigameType.POWER_SHAKE, 1, 30, null)
        );

        AlarmRequestDto request = new AlarmRequestDto(
                "Morning Gauntlet",
                LocalTime.of(6, 30),
                true,
                31,
                true,
                5,
                missions,
                true,
                90
        );

        AlarmResponseDto response = new AlarmResponseDto(
                1L,
                "Morning Gauntlet",
                LocalTime.of(6, 30),
                true,
                31,
                true,
                5,
                missions,
                true,
                90
        );

        when(alarmService.createAlarm(any(AlarmRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/alarms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Morning Gauntlet"))
                .andExpect(jsonPath("$.wakeUpCheckEnabled").value(true))
                .andExpect(jsonPath("$.wakeUpCheckDelayMinutes").value(5))
                .andExpect(jsonPath("$.missions.length()").value(2));
    }

    @Test
    @DisplayName("POST /api/v1/alarms returns 400 when missions list is empty")
    void createAlarm_EmptyMissions_Returns400() throws Exception {
        AlarmRequestDto invalidRequest = new AlarmRequestDto(
                "No Mission Alarm",
                LocalTime.of(6, 30),
                true,
                31,
                true,
                5,
                List.of(), // Violates @NotEmpty
                true,
                90
        );

        mockMvc.perform(post("/api/v1/alarms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.invalidFields.missions").exists());
    }
}