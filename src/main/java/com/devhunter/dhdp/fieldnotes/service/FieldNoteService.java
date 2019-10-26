package com.devhunter.dhdp.fieldnotes.service;

import com.devhunter.DHDPConnector4J.header.DHDPHeader;
import com.devhunter.DHDPConnector4J.request.DHDPRequestBody;
import com.devhunter.DHDPConnector4J.request.DHDPRequestType;
import com.devhunter.DHDPConnector4J.response.DHDPResponseBody;
import com.devhunter.DHDPConnector4J.response.DHDPResponseType;
import com.devhunter.dhdp.fieldnotes.model.FieldNote;
import com.devhunter.dhdp.infrastructure.DHDPService;
import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;
import com.devhunter.dhdp.services.MySqlService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static com.devhunter.DHDPConnector4J.constants.fieldNotes.FieldNotesConstants.*;
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
    public DHDPResponseBody login(DHDPRequestBody body) {
        DHDPResponseBody.Builder responseBodyBuilder = DHDPResponseBody.newBuilder();

        // create internal FieldNote
        FieldNote fieldNote = new FieldNote();
        fieldNote.setUsername(body.getString(USERNAME_KEY));
        fieldNote.setPassword(body.getString(PASSWORD_KEY));

        // ensure username was sent from client
        String userName = fieldNote.getUsername();
        if (userName == null) {
            String message = "No Username";

            responseBodyBuilder.setResponseType(DHDPResponseType.FAILURE);
            responseBodyBuilder.setMessage(message);
            return responseBodyBuilder.build();
        }
        // ensure password was sent from client
        String password = fieldNote.getPassword();
        if (password == null) {
            String message = "No Password";

            responseBodyBuilder.setResponseType(DHDPResponseType.FAILURE);
            responseBodyBuilder.setMessage(message);
            return responseBodyBuilder.build();
        }

        //make connection to database
        Connection connection = mMySqlService.getAwsConnection(DB_SERVER, DB_PORT, DB_DATABASE, DB_USERNAME, DB_PASSWORD);
        if (connection == null) {
            String message = "No Connection";
            mLogger.info(message);

            responseBodyBuilder.setResponseType(DHDPResponseType.FAILURE);
            responseBodyBuilder.setMessage(message);
            return responseBodyBuilder.build();
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

                responseBodyBuilder.setResponseType(DHDPResponseType.SUCCESS);
                responseBodyBuilder.setMessage(message);
                responseBodyBuilder.setResults(getResults(DHDPRequestType.LOGIN, resultSet));
                mMySqlService.closeConnection(connection);
                return responseBodyBuilder.build();
            }
        } catch (SQLException e) {
            mLogger.severe(e.toString());
            mMySqlService.closeConnection(connection);
        }
        String message = "Login Failed";
        mLogger.warning(message);

        responseBodyBuilder.setResponseType(DHDPResponseType.FAILURE);
        responseBodyBuilder.setMessage(message);
        return responseBodyBuilder.build();
    }

    @Override
    public DHDPResponseBody addNote(DHDPRequestBody body) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DHDPResponseBody updateNote(DHDPRequestBody body) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DHDPResponseBody deleteNote(DHDPRequestBody body) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DHDPResponseBody searchNote(DHDPRequestBody body) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DHDPResponseBody unsupportedNote(DHDPHeader header) {
        DHDPResponseBody.Builder response = DHDPResponseBody.newBuilder();
        response.setResponseType(DHDPResponseType.FAILURE);
        response.setMessage("Unsupported RequestType: " + header.getRequestType());
        response.setResults(null);
        return response.build();
    }

    /**
     * Retrieves results from a database query. All results should be received as a JSONArray
     *
     * @param requestType - helps determine what results are expected
     * @param resultSet   - contains the results of the db query
     * @return a map of the results
     * @throws SQLException if the results cannot be retrieved
     */
    private ArrayList<Map<String, String>> getResults(DHDPRequestType requestType, ResultSet resultSet)
            throws SQLException {
        ArrayList<Map<String, String>> results = new ArrayList<>();
        Map<String, String> resultMap = new HashMap<>();
        //build results from result set
        if (requestType.equals(DHDPRequestType.LOGIN)) {
            // result is login token
            resultMap.put(TOKEN_KEY, resultSet.getString(TOKEN_COLUMN));
        }

        results.add(resultMap);
        return results;
    }
}
