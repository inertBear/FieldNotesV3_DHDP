package com.devhunter.dhdp.services;

import com.devhunter.DHDPConnector4J.response.DHDPResponse;
import com.devhunter.DHDPConnector4J.response.DHDPResponseBody;
import com.devhunter.DHDPConnector4J.response.DHDPResponseType;
import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.devhunter.dhdp.fieldnotes.FieldNotesConstants.*;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MySqlServiceTest {
    private MySqlService mMySqlService;
    private static final String TEST_USERNAME = "keithh";
    private static final String TEST_PASSWORD = "hunterk";

    @Before
    public void setup() {
        DHDPServiceRegistry mRegistry = new DHDPServiceRegistry();
        MySqlService.initService(mRegistry);
        mMySqlService = mRegistry.resolve(MySqlService.class);
    }

    /**
     * test creating a connection to MySql database
     */
    @Test
    public void getFieldNoteDatabaseConnectionTest() {
        Connection connection = mMySqlService.getAwsConnection(DB_SERVER, DB_PORT, DB_DATABASE, DB_USERNAME, DB_PASSWORD);
        assertNotNull(connection);
    }

    /**
     * test sending a query to a MySql database
     */
    @Test
    public void executeFieldNoteLoginTest() throws SQLException {
        final String message = "Login Successful";
        final String loginQuery = "SELECT " + TOKEN_COLUMN + " FROM " + LOGIN_TABLE + " WHERE " + USERNAME_COLUMN +
                " = '" + TEST_USERNAME + "' AND " + PASSWORD_COLUMN + " = '" + TEST_PASSWORD + "'";
        final LocalDateTime now = LocalDateTime.now();

        Connection connection = mMySqlService.getAwsConnection(DB_SERVER, DB_PORT, DB_DATABASE, DB_USERNAME, DB_PASSWORD);
        ResultSet resultSet = mMySqlService.executeQuery(connection, loginQuery);

        if (resultSet.next()) {
            // get result
            Map<String, Object> result = new HashMap<>();
            result.put(TOKEN_KEY, resultSet.getString(TOKEN_COLUMN));
            List<Map<String, Object>> results = new ArrayList<>();
            results.add(result);

            // build FNResponse
            DHDPResponseBody.Builder responseBodyBuilder = DHDPResponseBody.newBuilder();
            responseBodyBuilder.setResponseType(DHDPResponseType.SUCCESS);
            responseBodyBuilder.setMessage(message);
            responseBodyBuilder.setResults(results);
            DHDPResponse response = DHDPResponse.newBuilder()
                    .setResponse(responseBodyBuilder.build())
                    .build();

            //close connection
            mMySqlService.closeConnection(connection);

            // assert
            assertEquals(DHDPResponseType.SUCCESS, response.getResponse().getResponseType());
            assertEquals(message, response.getResponse().getMessage());
            assertNotNull(response.getResponse().getResults());
        }
    }
}