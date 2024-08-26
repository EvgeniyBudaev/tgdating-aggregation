package com.tgdating.aggregation.service;

import com.tgdating.aggregation.dto.request.RequestProfileCreateDto;
import com.tgdating.aggregation.dto.request.RequestProfileFilterUpdateDto;
import com.tgdating.aggregation.dto.request.RequestProfileListGetDto;
import com.tgdating.aggregation.dto.request.RequestProfileNavigatorUpdateDto;
import com.tgdating.aggregation.dto.response.*;
import com.tgdating.aggregation.model.PaginationEntity;

import java.util.List;

public interface ProfileService {
    ResponseProfileCreateDto create(RequestProfileCreateDto requestProfileCreateDto);

    PaginationEntity<List<ResponseProfileListGetDto>> getProfileList(RequestProfileListGetDto requestProfileListGetDto);

    ResponseProfileBySessionIdGetDto getBySessionID(String sessionId, Double latitude, Double longitude);

    ResponseProfileFilterDto getFilterBySessionID(String sessionId, Double latitude, Double longitude);

    ResponseProfileFilterDto updateFilter(RequestProfileFilterUpdateDto requestProfileFilterUpdateDto);

    ResponseProfileNavigatorDto updateCoordinates(RequestProfileNavigatorUpdateDto requestProfileNavigatorUpdateDto);
}
