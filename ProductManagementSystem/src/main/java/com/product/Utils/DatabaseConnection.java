package com.product.Utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private final String url = "jdbc:postgresql://localhost:5432/store_db";
    private final String user = "gokulnathk";
    private final String password = "password";

    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static MongoClient mongoClient = null;
    private static MongoDatabase database = null;

    Connection conn = null;

    public static MongoDatabase getDatabase(String dbName) {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(CONNECTION_STRING);
        }
        if (database == null) {
            database = mongoClient.getDatabase(dbName);
        }
        return database;
    }

    public Connection connectPostgresDb() {
        try {
            conn = DriverManager.getConnection(url, user, password);
            return conn;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}
