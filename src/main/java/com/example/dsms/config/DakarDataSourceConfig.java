package com.example.dsms.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Objects;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.example.dsms.repository.dakar",
        entityManagerFactoryRef = "dakarEntityManagerFactory",
        transactionManagerRef = "dakarTransactionManager"
)
public class DakarDataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.dakar")
    public DataSourceProperties dakarDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "dakarDataSource")
    @Primary
    public DataSource dakarDataSource() {
        return dakarDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "dakarEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean dakarEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dakarDataSource());
        emf.setPackagesToScan("com.example.dsms.model");
        emf.setPersistenceUnitName("dakarPU");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.hbm2ddl.auto", "update");
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        emf.setJpaProperties(jpaProperties);


        return emf;
    }

    @Bean(name = "dakarTransactionManager")
    @Primary
    public PlatformTransactionManager dakarTransactionManager() {
        return new JpaTransactionManager(Objects.requireNonNull(dakarEntityManagerFactory().getObject()));
    }
}
