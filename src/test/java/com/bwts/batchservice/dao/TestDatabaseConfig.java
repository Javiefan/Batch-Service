package com.bwts.batchservice.dao;

import com.bwts.batchservice.jdbc.DatabaseConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
@ComponentScan(basePackages = "com.bwts.batchservice.dao",
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.bwts\\.batchservice\\..*Config"))
public class TestDatabaseConfig extends DatabaseConfig{

    @Bean
    public static PropertyPlaceholderConfigurer propConfig() {
        PropertyPlaceholderConfigurer ppc =  new PropertyPlaceholderConfigurer();
        ppc.setLocation(new ClassPathResource("application-test.properties"));
        return ppc;
    }

    @Value("${database.url}") private String url;
    @Value("${database.user}") private String user;
    @Value("${database.pass}") private String pass;

    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create().url(url).username(user).password(pass).build();
    }

}

