package com.devhunter.dhdp.services;

import com.devhunter.DHDPConnector4J.DHDPResponseType;
import com.devhunter.dhdp.fieldnotes.model.FieldNoteResponse;
import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.devhunter.DHDPConnector4J.constants.FieldNotesConstants.TOKEN_KEY;
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
            Map<String, String> result = new HashMap<>();
            result.put(TOKEN_KEY, resultSet.getString(TOKEN_COLUMN));

            // build FNResponse
            FieldNoteResponse fieldNoteResponse = new FieldNoteResponse();
            fieldNoteResponse.setStatus(DHDPResponseType.SUCCESS);
            fieldNoteResponse.setMessage(message);
            fieldNoteResponse.setTimestamp(now);
            fieldNoteResponse.addResults(result);

            //close connection
            mMySqlService.closeConnection(connection);

            // assert
            assertEquals(DHDPResponseType.SUCCESS, fieldNoteResponse.getStatus());
            assertEquals(message, fieldNoteResponse.getMessage());
            assertEquals(now, fieldNoteResponse.getTimestamp());
            assertNotNull(fieldNoteResponse.getResults());
        }
    }
}