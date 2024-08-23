package com.tgdating.aggregation.controller;

import com.tgdating.aggregation.dto.request.RequestProfileCreateDto;
import com.tgdating.aggregation.dto.request.RequestProfileListGetDto;
import com.tgdating.aggregation.dto.response.ResponseProfileBySessionIdGetDto;
import com.tgdating.aggregation.dto.response.ResponseProfileCreateDto;
import com.tgdating.aggregation.dto.response.ResponseProfileListGetDto;
import com.tgdating.aggregation.model.ProfileFilterEntity;
import com.tgdating.aggregation.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/profiles")
public class ProfileController {
    private final ProfileService profileService;
    private static final Path BASE_PROJECT_PATH = Paths.get(System.getProperty("user.dir"));

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping
    public ResponseEntity<ResponseProfileCreateDto> createProfile(
            @ModelAttribute RequestProfileCreateDto requestProfileCreateDto) {
        System.out.println("controller createProfile: " + requestProfileCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(profileService.create(requestProfileCreateDto));
    }

    @PostMapping("/list")
    public ResponseEntity<List<ResponseProfileListGetDto>> getProfileList(
            @RequestBody RequestProfileListGetDto requestProfileListGetDto
    ) {
        System.out.println("controller getProfileList sessionId: " + requestProfileListGetDto.getSessionId());
        return ResponseEntity.status(HttpStatus.OK).body(profileService.getProfileList(requestProfileListGetDto));
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<ResponseProfileBySessionIdGetDto> getProfileBySessionID(
            @PathVariable String sessionId,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude
    ) {
        System.out.println("controller getProfileBySessionID sessionId: " + sessionId);
        return ResponseEntity.status(HttpStatus.OK).body(profileService.getBySessionID(sessionId, latitude, longitude));
    }

    @GetMapping("/{sessionId}/filter")
    public ResponseEntity<ProfileFilterEntity> getFilterBySessionID(
            @PathVariable String sessionId,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude
    ) {
        System.out.println("controller getFilterBySessionID sessionId: " + sessionId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(profileService.getFilterBySessionID(sessionId, latitude, longitude));
    }

    @GetMapping(value = "/{sessionId}/image/{fileName}", produces = "image/webp")
    public byte[] getImageBySessionID(@PathVariable String sessionId, @PathVariable String fileName) throws IOException {
        Path staticFolderPath = BASE_PROJECT_PATH.resolve("src/main/resources/static/images");
        String filePath = String.format("%s/%s/%s", staticFolderPath, sessionId, fileName);
        System.out.println("getImage filePath: " + filePath);
        return Files.readAllBytes(Paths.get(filePath));
    }
}
