package com.tgdating.aggregation.repository;

import com.tgdating.aggregation.dto.request.*;
import com.tgdating.aggregation.model.*;

import java.util.List;

public interface ProfileRepository {
    ProfileEntity createProfile(RequestProfileCreateDto requestProfileCreateDto);

    ProfileEntity updateProfile(RequestProfileUpdateDto requestProfileUpdateDto);

    ProfileEntity deleteProfile(String sessionId);

    ProfileEntity blockProfile(String sessionId);

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

    ProfileNavigatorEntity deleteNavigator(String sessionId);

    ProfileNavigatorEntity findNavigatorBySessionID(String sessionId);

    ProfileNavigatorDetailEntity findNavigatorBetweenSessionIDAndViewedSessionID(
            ProfileNavigatorEntity profileNavigatorSessionEntity, ProfileNavigatorEntity profileNavigatorViewerEntity);

    ProfileFilterEntity addFilter(RequestProfileFilterAddDto requestProfileFilterAddDto);

    ProfileFilterEntity updateFilter(RequestProfileFilterUpdateDto requestProfileFilterUpdateDto);

    ProfileFilterEntity deleteFilter(String sessionId);

    ProfileFilterEntity findFilterBySessionID(String sessionId);

    ProfileTelegramEntity addTelegram(RequestProfileTelegramAddDto requestProfileTelegramAddDto);

    ProfileTelegramEntity updateTelegram(RequestProfileTelegramUpdateDto requestProfileTelegramUpdateDto);

    ProfileTelegramEntity deleteTelegram(String sessionId);

    ProfileTelegramEntity findTelegramBySessionID(String sessionId);

    ProfileLikeEntity addLike(RequestProfileLikeAddDto requestProfileLikeAddDto);

    ProfileLikeEntity findLikeBySessionID(String sessionId, String likedSessionId);

    ProfileBlockEntity addBlock(RequestProfileBlockAddDto requestProfileBlockAddDto);

    ProfileComplaintEntity addComplaint(RequestProfileComplaintAddDto requestProfileComplaintAddDto);

    Integer countComplaintsByCurrentMonthAndSessionID(String sessionId);
}
