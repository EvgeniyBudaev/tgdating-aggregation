package com.tgdating.aggregation.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseProfileTelegramDto {
    private String sessionId;
    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
    private String languageCode;
    private Boolean allowsWriteToPm;
    private String queryId;
    private Long chatId;
}
