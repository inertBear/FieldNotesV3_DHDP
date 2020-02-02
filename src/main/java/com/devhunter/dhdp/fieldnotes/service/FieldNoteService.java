package com.devhunter.dhdp.fieldnotes.service;

import com.devhunter.DHDPConnector4J.header.DHDPHeader;
import com.devhunter.DHDPConnector4J.model.GpsCoord;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static com.devhunter.dhdp.fieldnotes.FieldNotesConstants.*;

public class FieldNoteService extends DHDPService implements FNService {
    private static Logger mLogger = Logger.getLogger(FieldNoteService.class.getName());
    private MySqlService mMySqlService;
    private FieldNoteValidationService mValidationService;

    private FieldNoteService(String name, DHDPServiceRegistry registry) {
        super(name);
        mMySqlService = registry.resolve(MySqlService.class);
        mValidationService = registry.resolve(FieldNoteValidationService.class);
    }

    public static void initService(DHDPServiceRegistry registry) {
        if (!registry.containsService(FieldNoteService.class)) {
            registry.register(FieldNoteService.class, new FieldNoteService(FIELDNOTES_SERVICE_NAME, registry));
        }
    }

    @Override
    public DHDPResponseBody login(String username, String password) {
        DHDPResponseBody.Builder responseBodyBuilder = DHDPResponseBody.newBuilder();

        try {
            mValidationService.validateLogin(username, password);
        } catch (IllegalArgumentException e) {
            mLogger.warning("Failed to login: " + e.getMessage());
            responseBodyBuilder.setResponseType(DHDPResponseType.FAILURE);
            responseBodyBuilder.setMessage(e.getMessage());
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
        String loginQuery = "SELECT " + TOKEN_COLUMN +
                " FROM " + LOGIN_TABLE +
                " WHERE " + USERNAME_COLUMN + " = '" + username + "'" +
                " AND " + PASSWORD_COLUMN + " = '" + password + "'";
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
    public DHDPResponseBody addNote(String token, FieldNote fieldNote) {
        //TODO: should put the ticket number of the added FieldNote in the results
        DHDPResponseBody.Builder responseBodyBuilder = DHDPResponseBody.newBuilder();

        // validate add note entries
        try {
            mValidationService.validateAddNote(fieldNote);
        } catch (IllegalArgumentException e) {
            mLogger.warning("Failed to add FieldNote: " + e.getMessage());
            responseBodyBuilder.setResponseType(DHDPResponseType.FAILURE);
            responseBodyBuilder.setMessage(e.getMessage());
            return responseBodyBuilder.build();
        }

        // pull out start time and date
        LocalDateTime startTimestamp = fieldNote.getStartTimestamp();
        String startTime = startTimestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String startDate = startTimestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // pull out end time and date
        LocalDateTime endTimestamp = fieldNote.getStartTimestamp();
        String endTime = endTimestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String endDate = endTimestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // gps is not required - if provided, convert to (lat, long) string, else use "not provided"
        GpsCoord gps = fieldNote.getGps();
        String gpsString;
        if (gps != null) {
            gpsString = String.valueOf(gps.getLattitude()) + String.valueOf(gps.getLongitude());
        } else {
            gpsString = "Not Provided";
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

        String addTable = "Data_" + token;
        // send add executeQuery
        String addQuery = "INSERT INTO " + addTable +
                " (userName, wellName, dateStart, timeStart, mileageStart, description," +
                " mileageEnd, dateEnd, timeEnd, projectNumber, location, gps, billing)" +
                " VALUES (" +
                "'" + fieldNote.getUsername() + "'" + ", " +
                "'" + fieldNote.getWellname() + "'" + ", " +
                "'" + startDate + "'" + ", " +
                "'" + startTime + "'" + ", " +
                "'" + fieldNote.getMileageStart() + "'" + ", " +
                "'" + fieldNote.getDescription() + "'" + ", " +
                "'" + fieldNote.getMileageEnd() + "'" + ", " +
                "'" + endDate +
                "'" + ", " + "'" + endTime + "'" + ", " +
                "'" + fieldNote.getProject() + "'" + ", " +
                "'" + fieldNote.getLocation() + "'" + ", " +
                "'" + gpsString + "'" + ", " +
                "'" + fieldNote.getBilling() + "'" +
                ");";
        int numAlteredRows = mMySqlService.executeUpdate(connection, addQuery);

        // process add results
        if (numAlteredRows > 0) {
            String message = "FieldNote Added";

            String selectLastIdQuery = "SELECT LAST_INSERT_ID();";
            ResultSet resultSet = mMySqlService.executeQuery(connection, selectLastIdQuery);

            // process get last insert results
            try {
                if (resultSet != null && resultSet.next()) {
                    // build results (using ticket number of added FieldNote)
                    message = "Add Successful";

                    responseBodyBuilder.setResponseType(DHDPResponseType.SUCCESS);
                    responseBodyBuilder.setMessage(message);
                    responseBodyBuilder.setResults(getResults(DHDPRequestType.ADD, resultSet));
                    mMySqlService.closeConnection(connection);
                    return responseBodyBuilder.build();
                }
            } catch (SQLException e) {
                mLogger.severe(e.toString());
                mMySqlService.closeConnection(connection);
            }

            // build results (using num_of_affected_rows)
            ArrayList<Map<String, String>> results = new ArrayList<>();
            Map<String, String> result = new HashMap<>();
            result.put(NUMBER_AFFECTED_ROWS_KEY, String.valueOf(numAlteredRows));
            results.add(result);

            responseBodyBuilder.setResponseType(DHDPResponseType.SUCCESS);
            responseBodyBuilder.setMessage(message);
            responseBodyBuilder.setResults(results);
            mMySqlService.closeConnection(connection);
            return responseBodyBuilder.build();
        }

        String message = "Add Failed";
        mLogger.warning(message);

        responseBodyBuilder.setResponseType(DHDPResponseType.FAILURE);
        responseBodyBuilder.setMessage(message);
        return responseBodyBuilder.build();
    }

    @Override
    public DHDPResponseBody updateNote(DHDPRequestBody body) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DHDPResponseBody deleteNote(String token, int ticketNumber) {
        DHDPResponseBody.Builder responseBodyBuilder = DHDPResponseBody.newBuilder();

        // make connection to database
        Connection connection = mMySqlService.getAwsConnection(DB_SERVER, DB_PORT, DB_DATABASE, DB_USERNAME, DB_PASSWORD);
        if (connection == null) {
            String message = "No Connection";
            mLogger.info(message);

            responseBodyBuilder.setResponseType(DHDPResponseType.FAILURE);
            responseBodyBuilder.setMessage(message);
            return responseBodyBuilder.build();
        }

        String deleteTable = "Data_" + token;
        // send add executeQuery
        String deleteQuery = "DELETE FROM " + deleteTable + " WHERE ticketNumber = '" + ticketNumber + "'";
        int numDeletedRows = mMySqlService.executeUpdate(connection, deleteQuery);

        String message;
        if (numDeletedRows > 0) {
            message = "Delete Successful";
        } else {
            message = "Ticket does not exists";
        }

        // build results (number of tickets deleted)
        ArrayList<Map<String, String>> results = new ArrayList<>();
        Map<String, String> result = new HashMap<>();
        result.put(NUMBER_AFFECTED_ROWS_KEY, String.valueOf(numDeletedRows));
        results.add(result);

        responseBodyBuilder.setResponseType(DHDPResponseType.SUCCESS);
        responseBodyBuilder.setMessage(message);
        responseBodyBuilder.setResults(results);
        mMySqlService.closeConnection(connection);
        return responseBodyBuilder.build();
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
        } else if (requestType.equals(DHDPRequestType.ADD)) {
            resultMap.put(TICKET_NUMBER_KEY, resultSet.getString(NEW_TICKET_KEY));
        }

        results.add(resultMap);
        return results;
    }
}
