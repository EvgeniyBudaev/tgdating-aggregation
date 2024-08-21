package com.tgdating.aggregation.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileNavigatorEntity {
    private Long id;
    private String sessionId;
    private PointEntity location;
}
