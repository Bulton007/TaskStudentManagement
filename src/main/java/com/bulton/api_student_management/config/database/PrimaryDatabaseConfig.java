package com.bulton.api_student_management.config.database;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages =
        "com.bulton.api_student_management.repository",
    entityManagerFactoryRef =
        "primaryEntityManagerFactory",
    transactionManagerRef =
        "primaryTransactionManager"
)
public class PrimaryDatabaseConfig {

    @Bean(name = "primaryDataSource")
    @Primary
    public DataSource primaryDataSource(
        @Value("${app.datasource.primary.url}")
        String url,

        @Value("${app.datasource.primary.username}")
        String username,

        @Value("${app.datasource.primary.password}")
        String password,

        @Value(
            "${app.datasource.primary.driver-class-name}"
        )
        String driverClassName
    ) {
        HikariDataSource dataSource =
            new HikariDataSource();

        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        dataSource.setPoolName("PrimaryHikariPool");
        dataSource.setMinimumIdle(1);
        dataSource.setMaximumPoolSize(5);

        return dataSource;
    }

    @Bean(name = "primaryEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean
        primaryEntityManagerFactory(
            EntityManagerFactoryBuilder builder,

            @Qualifier("primaryDataSource")
            DataSource dataSource
        ) {

        return builder
            .dataSource(dataSource)
            .packages(
                "com.bulton.api_student_management.entity"
            )
            .persistenceUnit("primary")
            .properties(hibernateProperties())
            .build();
    }

    @Bean(name = "primaryTransactionManager")
    @Primary
    public PlatformTransactionManager
        primaryTransactionManager(
            @Qualifier("primaryEntityManagerFactory")
            EntityManagerFactory entityManagerFactory
        ) {

        return new JpaTransactionManager(
            entityManagerFactory
        );
    }

    private Map<String, Object> hibernateProperties() {
        Map<String, Object> properties =
            new HashMap<>();

        properties.put(
            "hibernate.hbm2ddl.auto",
            "update"
        );

        properties.put(
            "hibernate.dialect",
            "org.hibernate.dialect.PostgreSQLDialect"
        );

        properties.put(
            "hibernate.show_sql",
            "false"
        );

        properties.put(
            "hibernate.format_sql",
            "false"
        );

        return properties;
    }
}