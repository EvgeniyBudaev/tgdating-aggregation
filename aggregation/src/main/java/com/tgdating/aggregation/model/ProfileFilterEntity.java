package com.tgdating.aggregation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class ProfileFilterEntity {
    private Long id;
    private String sessionId;
    private String searchGender;
    private String lookingFor;
    private Integer ageFrom;
    private Integer ageTo;
    private Double distance;
    private Integer page;
    private Integer size;
}
