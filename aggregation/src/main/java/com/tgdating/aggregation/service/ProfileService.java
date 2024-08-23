package com.tgdating.aggregation.service;

import com.tgdating.aggregation.dto.request.RequestProfileCreateDto;
import com.tgdating.aggregation.dto.request.RequestProfileListGetDto;
import com.tgdating.aggregation.dto.response.ResponseProfileBySessionIdGetDto;
import com.tgdating.aggregation.dto.response.ResponseProfileCreateDto;
import com.tgdating.aggregation.dto.response.ResponseProfileListGetDto;
import com.tgdating.aggregation.model.ProfileFilterEntity;

import java.util.List;

public interface ProfileService {
    ResponseProfileCreateDto create(RequestProfileCreateDto requestProfileCreateDto);

    List<ResponseProfileListGetDto> getProfileList(RequestProfileListGetDto requestProfileListGetDto);

    ResponseProfileBySessionIdGetDto getBySessionID(String sessionId, Double latitude, Double longitude);

    ProfileFilterEntity getFilterBySessionID(String sessionId, Double latitude, Double longitude);
}
