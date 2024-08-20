package com.tgdating.aggregation.service;

import com.tgdating.aggregation.dto.request.RequestProfileCreateDto;
import com.tgdating.aggregation.model.ProfileEntity;

public interface ProfileService {
    ProfileEntity create(RequestProfileCreateDto requestProfileCreateDto);
}
