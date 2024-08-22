package com.tgdating.aggregation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class ProfileNavigatorEntity {
    private Long id;
    private String sessionId;
    private PointEntity location;
}
