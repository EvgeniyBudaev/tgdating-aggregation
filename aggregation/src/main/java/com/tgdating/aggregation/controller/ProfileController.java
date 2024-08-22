package com.tgdating.aggregation.controller;

import com.tgdating.aggregation.dto.request.RequestProfileCreateDto;
import com.tgdating.aggregation.dto.response.ResponseProfileBySessionIdGetDto;
import com.tgdating.aggregation.dto.response.ResponseProfileCreateDto;
import com.tgdating.aggregation.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/profiles")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping
    public ResponseEntity<ResponseProfileCreateDto> createProfile(@ModelAttribute RequestProfileCreateDto requestProfileCreateDto) {
        System.out.println("request: " + requestProfileCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(profileService.create(requestProfileCreateDto));
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<ResponseProfileBySessionIdGetDto> getProfileBySessionID(@PathVariable String sessionId) {
        System.out.println("getProfileBySessionID: " + sessionId);
        return ResponseEntity.status(HttpStatus.OK).body(profileService.getBySessionID(sessionId));
    }
}
