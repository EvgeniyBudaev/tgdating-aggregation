package com.tgdating.aggregation.service;

import com.tgdating.aggregation.dto.request.*;
import com.tgdating.aggregation.dto.response.*;
import com.tgdating.aggregation.model.PaginationEntity;

import java.util.List;

public interface ProfileService {
    ResponseProfileCreateDto create(RequestProfileCreateDto requestProfileCreateDto);

    PaginationEntity<List<ResponseProfileListGetDto>> getProfileList(RequestProfileListGetDto requestProfileListGetDto);

    ResponseProfileBySessionIdGetDto getBySessionID(String sessionId, Double latitude, Double longitude);

    ResponseProfileDetailGetDto getProfileDetail(
            String sessionId, String viewerSessionId, Double latitude, Double longitude);

    ResponseProfileShortInfoGetDto getProfileShortInfo(String sessionId, Double latitude, Double longitude);

    ResponseProfileFilterDto getFilterBySessionID(String sessionId, Double latitude, Double longitude);

    ResponseProfileFilterDto updateFilter(RequestProfileFilterUpdateDto requestProfileFilterUpdateDto);

    ResponseProfileNavigatorDto updateCoordinates(RequestProfileNavigatorUpdateDto requestProfileNavigatorUpdateDto);

    ResponseProfileLikeDto addLike(RequestProfileLikeAddDto requestProfileLikeAddDto);
}
