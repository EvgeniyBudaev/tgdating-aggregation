package com.tgdating.aggregation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ResponseProfileFilterDto {
    private String sessionId;
    private String searchGender;
    private String lookingFor;
    private Integer ageFrom;
    private Integer ageTo;
    private Double distance;
    private Integer page;
    private Integer size;
}
