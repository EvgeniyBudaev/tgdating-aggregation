package com.tgdating.aggregation.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestProfileComplaintAddDto {
    private String sessionId; // id того кто жалуется
    private String criminalSessionId; // id того на кого жалуются
    private String reason; // причина жалобы
}
