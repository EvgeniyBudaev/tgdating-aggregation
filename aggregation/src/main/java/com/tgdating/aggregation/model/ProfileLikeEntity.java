package com.tgdating.aggregation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@Builder
public class ProfileLikeEntity {
    private Long id;
    private String sessionId;
    private String likedSessionId;
    private Boolean isLiked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
