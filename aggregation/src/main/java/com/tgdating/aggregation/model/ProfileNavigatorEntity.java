package com.tgdating.aggregation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@Builder
public class ProfileNavigatorEntity {
    private Long id;
    private String sessionId;
    private PointEntity location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
