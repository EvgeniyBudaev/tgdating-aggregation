package com.tgdating.aggregation.dto.response;

import com.tgdating.aggregation.model.ProfileImageEntity;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ResponseProfileUpdateDto {
    private String sessionId;
    private String displayName;
    private LocalDate birthday;
    private String gender;
    private String location;
    private String description;
    private Double height;
    private Double weight;
    private Boolean isDeleted;
    private Boolean isBlocked;
    private Boolean isPremium;
    private Boolean isShowDistance;
    private Boolean isInvisible;
    private Boolean isOnline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastOnline;
    private List<ProfileImageEntity> images;
    private ResponseProfileNavigatorDto navigator;
    private ResponseProfileFilterDto filter;
    private ResponseProfileTelegramDto telegram;
}
