package com.tgdating.aggregation.service;

import com.tgdating.aggregation.dto.request.*;
import com.tgdating.aggregation.dto.response.*;
import com.tgdating.aggregation.model.PaginationEntity;
import com.tgdating.aggregation.model.ProfileBlockEntity;
import com.tgdating.aggregation.model.ProfileImageEntity;

import java.util.List;

public interface ProfileService {
    ResponseProfileCreateDto create(RequestProfileCreateDto requestProfileCreateDto);

    ResponseProfileUpdateDto update(RequestProfileUpdateDto requestProfileUpdateDto);

    void delete(String sessionId);

    PaginationEntity<List<ResponseProfileListGetDto>> getProfileList(RequestProfileListGetDto requestProfileListGetDto);

    ResponseProfileBySessionIdGetDto getBySessionID(String sessionId, Double latitude, Double longitude);

    ResponseProfileDetailGetDto getProfileDetail(
            String sessionId, String viewerSessionId, Double latitude, Double longitude);

    ResponseProfileShortInfoGetDto getProfileShortInfo(String sessionId, Double latitude, Double longitude);

    ProfileImageEntity deleteImage(Long id);

    ResponseProfileFilterDto getFilterBySessionID(String sessionId, Double latitude, Double longitude);

    ResponseProfileFilterDto updateFilter(RequestProfileFilterUpdateDto requestProfileFilterUpdateDto);

    ResponseProfileNavigatorDto updateCoordinates(RequestProfileNavigatorUpdateDto requestProfileNavigatorUpdateDto);

    ResponseProfileLikeDto addLike(RequestProfileLikeAddDto requestProfileLikeAddDto);

    ProfileBlockEntity addBlock(RequestProfileBlockAddDto requestProfileBlockAddDto);
}
