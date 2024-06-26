package com.quinbay.inventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/e_commerce");
        dataSource.setUsername("gokulnathk");
        dataSource.setPassword("password");
        return dataSource;
    }

    @Bean
    public Connection connection(DataSource dataSource) throws SQLException {
        return dataSource.getConnection();
    }
}
