package com.devhunter.dhdp.fieldnotes.service;

import com.devhunter.DHDPConnector4J.DHDPBody;
import com.devhunter.DHDPConnector4J.DHDPHeader;
import com.devhunter.DHDPConnector4J.DHDPRequestType;
import com.devhunter.DHDPConnector4J.DHDPResponseType;
import com.devhunter.dhdp.fieldnotes.model.FieldNoteResponse;
import com.devhunter.dhdp.infrastructure.DHDPService;
import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;
import com.devhunter.dhdp.services.MySqlService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static com.devhunter.DHDPConnector4J.constants.FieldNotesConstants.TOKEN_KEY;
import static com.devhunter.dhdp.fieldnotes.FieldNotesConstants.*;

public class FieldNoteService extends DHDPService implements FNService {
    private static Logger mLogger = Logger.getLogger(FieldNoteService.class.getName());
    private MySqlService mMySqlService;

    private FieldNoteService(String name, DHDPServiceRegistry registry) {
        super(name);
        mMySqlService = registry.resolve(MySqlService.class);
    }

    public static void initService(DHDPServiceRegistry registry) {
        if (!registry.containsService(FieldNoteService.class)) {
            registry.register(FieldNoteService.class, new FieldNoteService("FieldNotes", registry));
        }
    }

    @Override
    public FieldNoteResponse login(DHDPBody body) {
        FieldNoteResponse fieldNoteResponse = new FieldNoteResponse();

        // ensure username was sent from client
        String userName = body.getString(USERNAME_TAG);
        if (userName == null) {
            String message = "No Username";
            mLogger.info(message);

            fieldNoteResponse.setStatus(DHDPResponseType.FAILURE);
            fieldNoteResponse.setMessage(message);
            fieldNoteResponse.setTimestamp(LocalDateTime.now());
            return fieldNoteResponse;
        }
        // ensure password was sent from client
        String password = body.getString(PASSWORD_TAG);
        if (password == null) {
            String message = "No Password";
            mLogger.info(message);

            fieldNoteResponse.setStatus(DHDPResponseType.FAILURE);
            fieldNoteResponse.setMessage(message);
            fieldNoteResponse.setTimestamp(LocalDateTime.now());
            return fieldNoteResponse;
        }

        //make connection to database
        Connection connection = mMySqlService.getAwsConnection(DB_SERVER, DB_PORT, DB_DATABASE, DB_USERNAME, DB_PASSWORD);
        if (connection == null) {
            String message = "No Connection";
            mLogger.info(message);

            fieldNoteResponse.setStatus(DHDPResponseType.FAILURE);
            fieldNoteResponse.setMessage(message);
            fieldNoteResponse.setTimestamp(LocalDateTime.now());
            return fieldNoteResponse;
        }

        // send login executeQuery
        String loginQuery = "SELECT UserToken FROM MASTER_LOGIN" +
                " WHERE Username = '" + userName +
                "' AND Password = '" + password + "'";
        ResultSet resultSet = mMySqlService.executeQuery(connection, loginQuery);

        //process login results
        try {
            if (resultSet != null && resultSet.next()) {
                String message = "Login Successful";
                mLogger.info(message);

                fieldNoteResponse.setStatus(DHDPResponseType.SUCCESS);
                fieldNoteResponse.setMessage(message);
                fieldNoteResponse.setTimestamp(LocalDateTime.now());
                fieldNoteResponse.addResults(getResults(DHDPRequestType.LOGIN, resultSet));
                mMySqlService.closeConnection(connection);
                return fieldNoteResponse;
            }
        } catch (SQLException e) {
            mLogger.severe(e.toString());
            mMySqlService.closeConnection(connection);
        }
        String message = "Unable to Login";
        mLogger.warning(message);

        fieldNoteResponse.setStatus(DHDPResponseType.FAILURE);
        fieldNoteResponse.setMessage(message);
        fieldNoteResponse.setTimestamp(LocalDateTime.now());
        return fieldNoteResponse;
    }

    @Override
    public FieldNoteResponse addNote(DHDPBody body) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FieldNoteResponse updateNote(DHDPBody body) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FieldNoteResponse deleteNote(DHDPBody body) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FieldNoteResponse searchNote(DHDPBody body) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FieldNoteResponse unsupportedNote(DHDPBody body) {
        FieldNoteResponse fieldNoteResponse = new FieldNoteResponse();
        fieldNoteResponse.setStatus(DHDPResponseType.FAILURE);
        fieldNoteResponse.setMessage("Unsupported RequestType: " + body.get(DHDPHeader.REQUEST_TYPE_KEY));
        fieldNoteResponse.setTimestamp(LocalDateTime.now());
        fieldNoteResponse.addResults(null);
        return fieldNoteResponse;
    }

    /**
     * Retrieves results from a database query. All results should be received as a JSONArray
     *
     * @param requestType - helps determine what results are expected
     * @param resultSet   - contains the results of the db query
     * @return a map of the results
     * @throws SQLException if the results cannot be retrieved
     */
    private Map<String, String> getResults(DHDPRequestType requestType, ResultSet resultSet)
            throws SQLException {
        Map<String, String> resultMap = new HashMap<>();
        //build results from result set
        if (requestType.equals(DHDPRequestType.LOGIN)) {
            // result is login token
            resultMap.put(TOKEN_KEY, resultSet.getString(TOKEN_COLUMN));
        }
        return resultMap;
    }
}
