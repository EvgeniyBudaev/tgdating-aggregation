package com.tgdating.aggregation.config;

import io.github.cdimascio.dotenv.Dotenv;

public class ApiConfig {
    public static void loadEnv() {
        Dotenv dotenv = Dotenv.configure().load();

        String springDatasourceUrl = dotenv.get("SPRING_DATASOURCE_URL");
        String keycloakBaseUrl = dotenv.get("AGGREGATION_KEYCLOAK_BASE_URL");
        String keycloakRealm = dotenv.get("AGGREGATION_KEYCLOAK_REALM");
        String keycloakClientId = dotenv.get("AGGREGATION_KEYCLOAK_CLIENT_ID");
        String keycloakClientSecret = dotenv.get("AGGREGATION_KEYCLOAK_CLIENT_SECRET");
        String keycloakDefaultUserGroup = dotenv.get("AGGREGATION_KEYCLOAK_DEFAULT_USER_GROUP");
        String loggerLevel = dotenv.get("AGGREGATION_LOGGER_LEVEL");
        String port = dotenv.get("AGGREGATION_PORT");
        String postgresUser = dotenv.get("POSTGRES_USER");
        String postgresPassword = dotenv.get("POSTGRES_PASSWORD");

        System.setProperty("SPRING_DATASOURCE_URL", springDatasourceUrl);
        System.setProperty("AGGREGATION_KEYCLOAK_BASE_URL", keycloakBaseUrl);
        System.setProperty("AGGREGATION_KEYCLOAK_REALM", keycloakRealm);
        System.setProperty("AGGREGATION_KEYCLOAK_CLIENT_ID", keycloakClientId);
        System.setProperty("AGGREGATION_KEYCLOAK_CLIENT_SECRET", keycloakClientSecret);
        System.setProperty("AGGREGATION_KEYCLOAK_DEFAULT_USER_GROUP", keycloakDefaultUserGroup);
        System.setProperty("AGGREGATION_LOGGER_LEVEL", loggerLevel);
        System.setProperty("AGGREGATION_PORT", port);
        System.setProperty("POSTGRES_USER", postgresUser);
        System.setProperty("POSTGRES_PASSWORD", postgresPassword);
    }
}
