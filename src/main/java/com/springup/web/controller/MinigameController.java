package com.springup.web.controller;

import com.springup.domain.dto.MinigameDtos.*;
import com.springup.domain.enums.MinigameType;
import com.springup.service.MinigameEngine;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/minigames")
@RequiredArgsConstructor

public class MinigameController {

    private final MinigameEngine minigameEngine;

    @GetMapping("/challenge")
    public ResponseEntity<ChallengeResponse> getChallenge(
            @RequestParam MinigameType type,
            @RequestParam(defaultValue = "1") int difficulty) {
        return ResponseEntity.ok(minigameEngine.generateChallenge(type, difficulty));
    }

    @PostMapping("/verify")
    public ResponseEntity<VerificationResponse> verifySolution(
            @Valid @RequestBody VerificationRequest request) {
        return ResponseEntity.ok(minigameEngine.verifyAndDismiss(request));
    }
}
