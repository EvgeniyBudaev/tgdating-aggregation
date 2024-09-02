package com.tgdating.aggregation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

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
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
