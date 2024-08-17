package com.tgdating.aggregation.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ProfileEntity {
    private Long id;
    private String sessionId;
    private String displayName;
    private LocalDate birthday;
    private String gender;
    private String location;
    private String description;
    private double height;
    private double weight;
    private boolean isDeleted;
    private boolean isBlocked;
    private boolean isPremium;
    private boolean isShowDistance;
    private boolean isInvisible;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastOnline;
}
