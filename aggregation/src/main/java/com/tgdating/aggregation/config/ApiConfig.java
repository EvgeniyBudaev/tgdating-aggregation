package com.tgdating.aggregation.config;

import io.github.cdimascio.dotenv.Dotenv;

public class ApiConfig {
    public static void loadEnv() {
        Dotenv dotenv = Dotenv.configure().load();

        String springDatasourceUrl = dotenv.get("SPRING_DATASOURCE_URL");
        String postgresUser = dotenv.get("POSTGRES_USER");
        String postgresPassword = dotenv.get("POSTGRES_PASSWORD");

        System.setProperty("SPRING_DATASOURCE_URL", springDatasourceUrl);
        System.setProperty("POSTGRES_USER", postgresUser);
        System.setProperty("POSTGRES_PASSWORD", postgresPassword);
    }
}
