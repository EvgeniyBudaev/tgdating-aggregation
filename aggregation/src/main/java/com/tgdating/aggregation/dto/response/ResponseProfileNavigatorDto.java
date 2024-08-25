package com.tgdating.aggregation.dto.response;

import com.tgdating.aggregation.model.PointEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseProfileNavigatorDto {
    private String sessionId;
    private PointEntity location;
}
