package io.spaceandtime;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

// thread-safe singleton class
public class DataSource {
    private static volatile DataSource instance = null;
    private static volatile HikariDataSource ds = null;

    private DataSource() {
    }

    public static DataSource getInstance(String config) {
        if (instance == null) {
            synchronized (DataSource.class) {
                if (instance == null) {
                    instance = new DataSource();
                    instance.init(config);
                }
            }
        }
        return instance;
    }

    private void init(String config) {
        try {
            Properties properties = new Properties();
            InputStream inputStream = new FileInputStream(config);
            properties.load(inputStream);
            HikariConfig hikariConfig = new HikariConfig(properties);
            ds = new HikariDataSource(hikariConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
