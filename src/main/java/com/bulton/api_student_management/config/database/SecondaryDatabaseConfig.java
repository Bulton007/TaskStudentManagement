package com.bulton.api_student_management.config.database;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        "com.bulton.api_student_management.audit.repository",
    entityManagerFactoryRef =
        "secondaryEntityManagerFactory",
    transactionManagerRef =
        "secondaryTransactionManager"
)
public class SecondaryDatabaseConfig {

    @Bean(name = "secondaryDataSource")
    public DataSource secondaryDataSource(
        @Value("${app.datasource.secondary.url}")
        String url,

        @Value("${app.datasource.secondary.username}")
        String username,

        @Value("${app.datasource.secondary.password}")
        String password,

        @Value(
            "${app.datasource.secondary.driver-class-name}"
        )
        String driverClassName
    ) {
        HikariDataSource dataSource =
            new HikariDataSource();

        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        dataSource.setPoolName("SecondaryHikariPool");
        dataSource.setMinimumIdle(1);
        dataSource.setMaxLifetime(5);

        return dataSource;
    }

    @Bean(name = "secondaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean
        secondaryEntityManagerFactory(
            EntityManagerFactoryBuilder builder,

            @Qualifier("secondaryDataSource")
            DataSource dataSource
        ) {

        return builder
            .dataSource(dataSource)
            .packages(
                "com.bulton.api_student_management.audit.entity"
            )
            .persistenceUnit("secondary")
            .properties(hibernateProperties())
            .build();
    }

    @Bean(name = "secondaryTransactionManager")
    public PlatformTransactionManager
        secondaryTransactionManager(
            @Qualifier("secondaryEntityManagerFactory")
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