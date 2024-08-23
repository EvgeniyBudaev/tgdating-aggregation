package com.tgdating.aggregation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@Builder
public class ProfileListEntity {
    private String sessionId;
    private Double distance;
    private LocalDateTime lastOnline;
}
