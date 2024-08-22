package com.tgdating.aggregation.dto.request;

import lombok.Data;

@Data
public class RequestProfileTelegramAddDto {
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
