package com.cartdetox.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * When DATABASE_URL is set (Railway PostgreSQL), override JPA dialect to PostgreSQL.
 * The URL format from Railway is: postgresql://user:pass@host:port/db
 * Spring Boot handles the JDBC URL translation automatically via the datasource URL.
 */
@Configuration
public class PostgresConfig {
    // Railway sets DATABASE_URL env var automatically.
    // application.yml reads it via ${DATABASE_URL:jdbc:h2:mem:cartdetox}
    // No extra config needed — Spring Boot auto-detects PostgreSQL driver from URL.
}
