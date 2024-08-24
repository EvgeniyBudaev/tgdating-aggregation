package com.tgdating.aggregation.shared;

import java.time.format.DateTimeFormatter;

public class Constants {
    public static final DateTimeFormatter YYYY_MM_DD_T_HH_MM_SS_Z = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    public static final String DEFAULT_AGE_FROM = "18";
    public static final String DEFAULT_AGE_TO = "100";
    public static final String DEFAULT_DISTANCE = "30";
    public static final String DEFAULT_LOOKING_FOR = "all";
    public static final String DEFAULT_PAGE = "1";
    public static final String DEFAULT_PAGE_SIZE = "50";
    public static final String DEFAULT_SEARCH_GENDER = "all";
}
