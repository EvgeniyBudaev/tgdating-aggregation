package com.tgdating.aggregation.repository;

import com.tgdating.aggregation.dto.request.*;
import com.tgdating.aggregation.model.*;

import java.util.List;

public interface ProfileRepository {
    ProfileEntity create(RequestProfileCreateDto requestProfileCreateDto);

    void updateLastOnline(String sessionId);

    ProfileImageEntity addImage(RequestProfileImageAddDto requestProfileImageAddDto);

    ProfileNavigatorEntity addNavigator(RequestProfileNavigatorAddDto requestProfileNavigatorAddDto);

    void updateNavigator(RequestProfileNavigatorAddDto requestProfileNavigatorAddDto);

    ProfileFilterEntity addFilter(RequestProfileFilterAddDto requestProfileFilterAddDto);

    ProfileTelegramEntity addTelegram(RequestProfileTelegramAddDto requestProfileTelegramAddDto);

    ProfileEntity findBySessionID(String sessionId);

    List<ProfileImageEntity> findImageListBySessionID(String sessionId);

    ProfileNavigatorEntity findNavigatorBySessionID(String sessionId);

    ProfileFilterEntity findFilterBySessionID(String sessionId);

    ProfileTelegramEntity findTelegramBySessionID(String sessionId);
}
