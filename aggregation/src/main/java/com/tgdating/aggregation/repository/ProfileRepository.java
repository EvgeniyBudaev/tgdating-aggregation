package com.tgdating.aggregation.repository;

import com.tgdating.aggregation.dto.request.RequestProfileCreateDto;
import com.tgdating.aggregation.dto.request.RequestProfileImageAddDto;
import com.tgdating.aggregation.model.ProfileEntity;
import com.tgdating.aggregation.model.ProfileImageEntity;

public interface ProfileRepository {
    ProfileEntity create(RequestProfileCreateDto requestProfileCreateDto);

    ProfileImageEntity addImage(RequestProfileImageAddDto requestProfileImageAddDto);
}
