package com.tgdating.aggregation.service;

import com.tgdating.aggregation.dto.request.RequestProfileCreateDto;
import com.tgdating.aggregation.model.ProfileEntity;
import com.tgdating.aggregation.repository.ProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public ProfileEntity create(RequestProfileCreateDto requestProfileCreateDto) {
        return  profileRepository.create(requestProfileCreateDto);
    }
}
