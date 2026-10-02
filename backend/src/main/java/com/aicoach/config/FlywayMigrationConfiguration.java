package com.aicoach.config;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Runs schema migrations explicitly because Spring Boot 4 no longer auto-configures Flyway.
 */
@Configuration
public class FlywayMigrationConfiguration {

    @Bean(initMethod = "migrate")
    Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .baselineVersion("6")
                .cleanDisabled(true)
                .load();
    }
}
