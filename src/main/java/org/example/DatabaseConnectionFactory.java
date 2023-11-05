package org.example;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseConnectionFactory {

    private BasicDataSource writeDataSource = null;
    private List<BasicDataSource> readDataSources = new ArrayList<>();
    private AtomicInteger readIndex = new AtomicInteger(0);
    // RDS 实例连接信息

    String port = "3306";
    String databaseName = "albumstore";
    String JDBC_USER = "admin";
    String JDBC_PASSWORD = "12345678";

    public DatabaseConnectionFactory(String writeEndpoint, List<String> readEndpoints) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://" + writeEndpoint +":" + port + "/"+databaseName);
        dataSource.setUsername(JDBC_USER);
        dataSource.setPassword(JDBC_PASSWORD);
        dataSource.setInitialSize(5);
        dataSource.setMaxTotal(100);
        dataSource.setMaxIdle(50);
        dataSource.setMinIdle(2);
        dataSource.setMaxWaitMillis(100);
        this.writeDataSource = dataSource;
        // Configure read (replicas) data sources
        for (String readEndpoint : readEndpoints) {
            BasicDataSource readConfig = new BasicDataSource();
            readConfig.setUrl("jdbc:mysql://" + readEndpoint +":" + port + "/"+databaseName);
            readConfig.setUsername(JDBC_USER);
            readConfig.setPassword(JDBC_PASSWORD);
            readConfig.setInitialSize(5);
            readConfig.setMaxTotal(100);
            readConfig.setMaxIdle(50);
            readConfig.setMinIdle(2);
            readConfig.setMaxWaitMillis(100);
            this.readDataSources.add(readConfig);
        }
    }

    public Connection getReadConnection() throws Exception {
        // 简单的轮询策略
        int index = readIndex.getAndIncrement() % readDataSources.size();
        BasicDataSource readDataSource = readDataSources.get(index);
        return readDataSource.getConnection();
    }

    public Connection getWriteConnection() throws Exception {
        return writeDataSource.getConnection();
    }
}

