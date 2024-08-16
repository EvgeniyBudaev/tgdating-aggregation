package com.tgdating.aggregation;

import com.tgdating.aggregation.config.ApiConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AggregationApplication {

    public static void main(String[] args) {
        ApiConfig.loadEnv();
        SpringApplication.run(AggregationApplication.class, args);
    }

}
