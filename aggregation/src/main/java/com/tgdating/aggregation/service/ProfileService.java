package com.tgdating.aggregation.service;

import com.tgdating.aggregation.dto.request.RequestProfileCreateDto;
import com.tgdating.aggregation.dto.response.ResponseProfileBySessionIdGetDto;
import com.tgdating.aggregation.dto.response.ResponseProfileCreateDto;

public interface ProfileService {
    ResponseProfileCreateDto create(RequestProfileCreateDto requestProfileCreateDto);

    ResponseProfileBySessionIdGetDto getBySessionID(String sessionId);
}
