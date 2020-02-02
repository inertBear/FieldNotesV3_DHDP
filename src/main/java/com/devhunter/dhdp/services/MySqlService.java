package com.devhunter.dhdp.services;

import com.devhunter.dhdp.infrastructure.DHDPService;
import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;

import java.sql.*;
import java.util.logging.Logger;

import static com.devhunter.dhdp.infrastructure.DHDPConstants.MYSQL_SERVICE_NAME;

/**
 * Makes connections to a MySQL database and preforms CRUD operations
 * Concerning Unit testing - this can't be tested in isolation without
 * a local mysql database instance
 */
public class MySqlService extends DHDPService {
    private static final String JDBC_DRIVER_CLASSNAME = "com.mysql.jdbc.Driver";
    private static final String URL_TEMPLATE = "jdbc:mysql://<ENDPOINT>:<PORT>/<DBNAME>" +
            "?verifyServerCertificate=true&useSSL=true&requireSSL=true";
    private static final Logger mLogger = Logger.getLogger(MySqlService.class.getName());

    private MySqlService(String name) {
        super(name);
    }

    public static void initService(DHDPServiceRegistry registry) {
        if (!registry.containsService(MySqlService.class)) {
            registry.register(MySqlService.class, new MySqlService(MYSQL_SERVICE_NAME));
        }
    }

    /**
     * make a connection to an Amazon Web Services Relational DataBase
     *
     * @param endpoint   endpoint/location of the database
     * @param dbName     name of the database to connect to
     * @param dbUsername database username
     * @param dbPassword database apssword
     * @return a connection to executeQuery against
     */
    public Connection getAwsConnection(String endpoint, int port, String dbName, String dbUsername, String dbPassword) {
        try {
            Class.forName(JDBC_DRIVER_CLASSNAME);
            String url = buildAwsUrl(endpoint, port, dbName);
            return DriverManager.getConnection(url, dbUsername, dbPassword);
        } catch (ClassNotFoundException | SQLException e) {
            mLogger.severe(e.toString());
        }
        return null;
    }

    /**
     * build a url from a AWS endpoint
     *
     * @param endpoint of the connection
     * @param port     to connect with
     * @param dbName   to connect to
     * @return url string to make a connection with
     */
    private String buildAwsUrl(String endpoint, int port, String dbName) {
        String url = URL_TEMPLATE.replace("<ENDPOINT>", endpoint);
        url = url.replace("<PORT>", String.valueOf(port));
        url = url.replace("<DBNAME>", dbName);
        return url;
    }

    /**
     * sends a SELECT statement to the connected database
     *
     * @param connection to send executeQuery with
     * @param query      to be queried on connected database
     */
    public ResultSet executeQuery(Connection connection, String query) {
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            mLogger.severe(e.toString());
        }
        return null;
    }

    /**
     * send an INSERT, UPDATE, or DELETE statement to the connected database
     *
     * @param connection to send executeQuery with
     * @param query      to be updated on connected database
     * @return the number of rows affected by the update
     */
    public int executeUpdate(Connection connection, String query) {
        try {
            Statement smt = connection.createStatement();
            return smt.executeUpdate(query);
        } catch (SQLException e) {
            mLogger.severe(e.toString());
        }
        return 0;
    }

    /**
     * closes a connection to a database
     *
     * @param connection to be closed
     */
    public void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            mLogger.severe(e.toString());
        }
    }
}
