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
        basePackages = "com.example.dsms.repository.thies",
        entityManagerFactoryRef = "thiesEntityManagerFactory",
        transactionManagerRef = "thiesTransactionManager"
)
public class ThiesDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.thies")
    public DataSourceProperties thiesDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "thiesDataSource")
    public DataSource thiesDataSource() {
        return thiesDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "thiesEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean thiesEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(thiesDataSource());
        emf.setPackagesToScan("com.example.dsms.model");
        emf.setPersistenceUnitName("thiesPU");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.hbm2ddl.auto", "update");
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        emf.setJpaProperties(jpaProperties);


        return emf;
    }

    @Bean(name = "thiesTransactionManager")
    public PlatformTransactionManager thiesTransactionManager() {
        return new JpaTransactionManager(Objects.requireNonNull(thiesEntityManagerFactory().getObject()));
    }
}
