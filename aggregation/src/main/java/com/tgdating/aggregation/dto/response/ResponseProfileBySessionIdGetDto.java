package com.tgdating.aggregation.dto.response;

import com.tgdating.aggregation.model.ProfileNavigatorEntity;
import com.tgdating.aggregation.model.ProfileTelegramEntity;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ResponseProfileBySessionIdGetDto {
    private Long id;
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastOnline;
    private ProfileNavigatorEntity navigator;
    private ProfileTelegramEntity telegram;
}
