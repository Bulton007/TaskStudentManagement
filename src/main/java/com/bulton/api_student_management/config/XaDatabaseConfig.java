package com.bulton.api_student_management.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.hibernate.ConfigurableJtaPlatform;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.atomikos.jdbc.AtomikosDataSourceBean;

@Configuration (proxyBeanMethods = false)
@EnableTransactionManagement 
public class XaDatabaseConfig {
    @Bean (
        name = "primaryDataSource", 
        initMethod = "init", 
        destroyMethod = "close"
    )
    @Primary 
    @DependsOn ("userTransactionService")
    public AtomikosDataSourceBean primaryDataSource(
        @Value ("${app.datasource.primary.url}") String url, 
        @Value ("${app.datasource.primary.username}") String username, 
        @Value ("${app.datasource.primary.password}") String password
    ){
        return createXaDataSource(
            "student-database-xa", 
            url,
            username, 
            password);
    }

    @Bean (
        name = "secondaryDataSource", 
        initMethod = "init",
        destroyMethod = "close"
    )
    @DependsOn ("userTransactionService")
    public AtomikosDataSourceBean secondaryDataSource(
        @Value ("${app.datasource.secondary.url}")String url,
        @Value ("${app.datasource.secondary.username}")String username, 
        @Value ("${app.datasource.secondary.password}")String password
    ){
        return createXaDataSource(
            "audit-database-xa", 
            url,
            username, 
            password
        ); 
    }
        private AtomikosDataSourceBean createXaDataSource(
        String resourceName,
        String url,
        String username,
        String password
    ) {
        Properties xaProperties = new Properties();
        xaProperties.setProperty("URL", url);
        xaProperties.setProperty("user", username);
        xaProperties.setProperty("password", password);

        AtomikosDataSourceBean dataSource =
            new AtomikosDataSourceBean();

        dataSource.setUniqueResourceName(resourceName);

        dataSource.setXaDataSourceClassName(
            "org.postgresql.xa.PGXADataSource"
        );

        dataSource.setXaProperties(xaProperties);
        dataSource.setMinPoolSize(1);
        dataSource.setMaxPoolSize(5);
        dataSource.setBorrowConnectionTimeout(30);

        return dataSource;
    }

    @Bean (name = "primaryEntityManagerFactory")
    @Primary 
    public LocalContainerEntityManagerFactoryBean 
        primaryEntityManagerFactory(
            EntityManagerFactoryBuilder builder, 
            @Qualifier("primaryDataSource")DataSource dataSource, 
            @Qualifier("transactionManager")JtaTransactionManager transactionManager
        ){
            return builder
                .dataSource(dataSource)
                .jta(true)
                .packages(
                    "com.bulton.api_student_management.entity"
                )
                .persistenceUnit("primary")
                .properties(jpaProperties(transactionManager))
                .build();
        }
    @Bean(name = "secondaryEntityManagerFactory")
public LocalContainerEntityManagerFactoryBean
    secondaryEntityManagerFactory(
        EntityManagerFactoryBuilder builder,
        @Qualifier("secondaryDataSource") DataSource dataSource,
        @Qualifier("transactionManager")
        JtaTransactionManager transactionManager
    ) {
    return builder
        .dataSource(dataSource)
        .jta(true)
        .packages(
            "com.bulton.api_student_management.audit.entity"
        )
        .persistenceUnit("secondary")
        .properties(jpaProperties(transactionManager))
        .build();
}
    private Map<String, Object> jpaProperties(
        JtaTransactionManager transactionManager
    ){
        Map<String, Object> properties = new HashMap<>(); 
         properties.put(
            "hibernate.hbm2ddl.auto",
            "validate"
        );

        properties.put(
            "hibernate.dialect",
            "org.hibernate.dialect.PostgreSQLDialect"
        );

        properties.put(
            "hibernate.transaction.coordinator_class",
            "jta"
        );

        properties.put(
            "hibernate.transaction.jta.platform",
            new ConfigurableJtaPlatform(
                transactionManager.getTransactionManager(),
                transactionManager.getUserTransaction(),
                transactionManager.getTransactionSynchronizationRegistry()
            )
        );

        properties.put(
            "hibernate.connection.handling_mode",
            "DELAYED_ACQUISITION_AND_RELEASE_AFTER_STATEMENT"
        );

        properties.put("hibernate.show_sql", false);
        properties.put("hibernate.format_sql", false);

        return properties;
    }
     @Configuration(proxyBeanMethods = false)
    @EnableJpaRepositories(
        basePackages =
            "com.bulton.api_student_management.repository",
        entityManagerFactoryRef =
            "primaryEntityManagerFactory",
        transactionManagerRef =
            "transactionManager"
    )
    public static class PrimaryRepositories {
    }

    @Configuration(proxyBeanMethods = false)
    @EnableJpaRepositories(
        basePackages =
            "com.bulton.api_student_management.audit.repository",
        entityManagerFactoryRef =
            "secondaryEntityManagerFactory",
        transactionManagerRef =
            "transactionManager"
    )
    public static class SecondaryRepositories {
    }
    


}
