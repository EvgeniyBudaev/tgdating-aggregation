package com.tgdating.aggregation.repository;

import com.tgdating.aggregation.dto.request.*;
import com.tgdating.aggregation.model.*;

public interface ProfileRepository {
    ProfileEntity create(RequestProfileCreateDto requestProfileCreateDto);

    ProfileImageEntity addImage(RequestProfileImageAddDto requestProfileImageAddDto);

    ProfileNavigatorEntity addNavigator(RequestProfileNavigatorAddDto requestProfileNavigatorAddDto);

    ProfileFilterEntity addFilter(RequestProfileFilterAddDto requestProfileFilterAddDto);

    ProfileTelegramEntity addTelegram(RequestProfileTelegramAddDto requestProfileTelegramAddDto);

    ProfileEntity findBySessionID(String sessionId);

    ProfileTelegramEntity findTelegramBySessionID(String sessionId);
}
