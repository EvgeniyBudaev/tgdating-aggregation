package com.tgdating.aggregation.repository;

import com.tgdating.aggregation.dto.request.*;
import com.tgdating.aggregation.model.*;

import java.util.List;

public interface ProfileRepository {
    ProfileEntity create(RequestProfileCreateDto requestProfileCreateDto);

    ProfileEntity update(RequestProfileUpdateDto requestProfileUpdateDto);

    PaginationEntity<List<ProfileListEntity>> findProfileList(RequestProfileListGetDto requestProfileListGetDto);

    ProfileEntity findBySessionID(String sessionId);

    void updateLastOnline(String sessionId);

    ProfileImageEntity addImage(RequestProfileImageAddDto requestProfileImageAddDto);

    ProfileImageEntity updateImage(RequestProfileImageUpdateDto requestProfileImageUpdateDto);

    ProfileImageEntity deleteImage(Long id);

    List<ProfileImageEntity> findImageListBySessionID(String sessionId);

    List<ProfileImageEntity> findImagePublicListBySessionID(String sessionId);

    ProfileImageEntity findImageByID(Long id);

    ProfileNavigatorEntity addNavigator(RequestProfileNavigatorAddDto requestProfileNavigatorAddDto);

    ProfileNavigatorEntity updateNavigator(RequestProfileNavigatorUpdateDto requestProfileNavigatorUpdateDto);

    ProfileNavigatorEntity findNavigatorBySessionID(String sessionId);

    ProfileFilterEntity addFilter(RequestProfileFilterAddDto requestProfileFilterAddDto);

    ProfileFilterEntity updateFilter(RequestProfileFilterUpdateDto requestProfileFilterUpdateDto);

    ProfileFilterEntity findFilterBySessionID(String sessionId);

    ProfileTelegramEntity addTelegram(RequestProfileTelegramAddDto requestProfileTelegramAddDto);

    ProfileTelegramEntity updateTelegram(RequestProfileTelegramUpdateDto requestProfileTelegramUpdateDto);

    ProfileTelegramEntity findTelegramBySessionID(String sessionId);

    ProfileLikeEntity addLike(RequestProfileLikeAddDto requestProfileLikeAddDto);

    ProfileLikeEntity findLikeBySessionID(String sessionId, String likedSessionId);
}
