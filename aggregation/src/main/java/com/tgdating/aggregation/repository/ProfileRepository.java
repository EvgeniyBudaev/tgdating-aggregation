package com.tgdating.aggregation.repository;

import com.tgdating.aggregation.dto.request.RequestProfileCreateDto;
import com.tgdating.aggregation.model.ProfileEntity;

public interface ProfileRepository {
    ProfileEntity create(RequestProfileCreateDto requestProfileCreateDto);
}
