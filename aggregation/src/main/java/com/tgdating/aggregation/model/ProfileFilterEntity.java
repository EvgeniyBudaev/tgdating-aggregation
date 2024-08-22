package com.tgdating.aggregation.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileFilterEntity {
    private Long id;
    private String sessionId;
    private String searchGender;
    private String lookingFor;
    private Byte ageFrom;
    private Byte ageTo;
    private Double distance;
    private Integer page;
    private Integer size;
}
