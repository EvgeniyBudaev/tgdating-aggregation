package com.tgdating.aggregation.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PointEntity {
    private Double latitude;
    private Double longitude;
}
