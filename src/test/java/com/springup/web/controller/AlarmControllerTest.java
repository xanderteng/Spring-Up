package com.springup.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springup.domain.dto.AlarmRequestDto;
import com.springup.domain.dto.AlarmResponseDto;
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
    @DisplayName("POST /api/v1/alarms should return 201 Created when valid payload provided")
    void createAlarm_ValidPayload_Returns201() throws Exception {
        AlarmRequestDto request = new AlarmRequestDto(
                "Morning Raid",
                LocalTime.of(6, 30),
                true,
                31, // Weekdays mask
                MinigameType.MATH,
                2,
                3,
                null,
                true,
                90
        );

        AlarmResponseDto response = new AlarmResponseDto(
                1L,
                "Morning Raid",
                LocalTime.of(6, 30),
                true,
                31,
                MinigameType.MATH,
                2,
                3,
                true,
                90
        );

        when(alarmService.createAlarm(any(AlarmRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/alarms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Morning Raid"))
                .andExpect(jsonPath("$.minigame").value("MATH"));
    }

    @Test
    @DisplayName("POST /api/v1/alarms should return 400 Bad Request when title is blank")
    void createAlarm_InvalidTitle_Returns400() throws Exception {
        AlarmRequestDto invalidRequest = new AlarmRequestDto(
                "", // Blank title violates @NotBlank
                LocalTime.of(6, 30),
                true,
                31,
                MinigameType.MATH,
                2,
                3,
                null,
                true,
                90
        );

        mockMvc.perform(post("/api/v1/alarms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.invalidFields.title").exists());
    }
}