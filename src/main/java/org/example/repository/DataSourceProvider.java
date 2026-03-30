package org.example.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DataSourceProvider {

    private final Properties dbProperties;
    private DataSource dataSource;
    public static final DataSourceProvider Instance = new DataSourceProvider();

    public DataSourceProvider() {
        this.dbProperties = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new IllegalStateException("db.properties not found in classpath");
            }
            dbProperties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load db.properties", e);
        }
    }

    // Task 1 - Returns a DataSource connected to the database defined in db.properties
    public DataSource getDataSource() {
        if (dataSource == null) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dbProperties.getProperty("db.url"));
            config.setUsername(dbProperties.getProperty("db.username"));
            config.setPassword(dbProperties.getProperty("db.password"));
            config.setDriverClassName(dbProperties.getProperty("db.driver"));
            config.setMaximumPoolSize(10);
            dataSource = new HikariDataSource(config);
        }
        return dataSource;
    }
}
