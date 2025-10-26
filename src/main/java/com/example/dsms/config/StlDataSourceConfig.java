package com.example.dsms.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        basePackages = "com.example.dsms.repository.stl",
        entityManagerFactoryRef = "stlEntityManagerFactory",
        transactionManagerRef = "stlTransactionManager"
)
public class StlDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.stl")
    public DataSourceProperties stlDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "stlDataSource")
    public DataSource stlDataSource() {
        return stlDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "stlEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean stlEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(stlDataSource());
        emf.setPackagesToScan("com.example.dsms.model");
        emf.setPersistenceUnitName("stlPU");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.hbm2ddl.auto", "update");
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        emf.setJpaProperties(jpaProperties);


        return emf;
    }

    @Bean(name = "stlTransactionManager")
    public PlatformTransactionManager stlTransactionManager() {
        return new JpaTransactionManager(Objects.requireNonNull(stlEntityManagerFactory().getObject()));
    }
}
