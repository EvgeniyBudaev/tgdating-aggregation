package com.tgdating.aggregation.controller;

import com.tgdating.aggregation.dto.request.RequestProfileCreateDto;
import com.tgdating.aggregation.model.ProfileEntity;
import com.tgdating.aggregation.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping
    public ResponseEntity<ProfileEntity> create(@RequestBody RequestProfileCreateDto requestProfileCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(profileService.create(requestProfileCreateDto));
    }
}
