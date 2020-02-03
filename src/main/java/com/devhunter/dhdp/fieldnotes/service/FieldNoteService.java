package com.devhunter.dhdp.fieldnotes.service;

import com.devhunter.DHDPConnector4J.header.DHDPHeader;
import com.devhunter.DHDPConnector4J.model.GpsCoord;
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
    private FieldNoteQueryService mQueryService;

    private FieldNoteService(String name, DHDPServiceRegistry registry) {
        super(name);
        mMySqlService = registry.resolve(MySqlService.class);
        mValidationService = registry.resolve(FieldNoteValidationService.class);
        mQueryService = registry.resolve(FieldNoteQueryService.class);
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
        if (token != null) {
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
                    "'" + fieldNote.getBillingType() + "'" +
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
                ArrayList<Map<String, Object>> results = new ArrayList<>();
                Map<String, Object> result = new HashMap<>();
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
        } else {
            return malformedNote("No User Token");
        }
    }

    @Override
    public DHDPResponseBody updateNote(String token, int ticketNumber, FieldNote update) {
        if (token != null) {
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

            //search for ticket by ticket number
            FieldNote original;
            try {
                original = searchNote(connection, token, ticketNumber);
            } catch (Exception e) {
                mLogger.info(e.getMessage());

                responseBodyBuilder.setResponseType(DHDPResponseType.FAILURE);
                responseBodyBuilder.setMessage(e.getMessage());
                return responseBodyBuilder.build();
            }

            // merge notes to create update
            FieldNote updatedNote = mergeFieldNotes(original, update);

            // pull out start time and date
            LocalDateTime startTimestamp = updatedNote.getStartTimestamp();
            String updatedStartTime = startTimestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String updateStartDate = startTimestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // pull out end time and date
            LocalDateTime endTimestamp = updatedNote.getStartTimestamp();
            String updatedEndTime = endTimestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String updatedEndDate = endTimestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // pull out gps string
            GpsCoord gps = updatedNote.getGps();
            String gpsString;
            if (gps != null) {
                gpsString = String.valueOf(gps.getLattitude()) + String.valueOf(gps.getLongitude());
            } else {
                gpsString = "Not Provided";
            }

            String updateTable = "Data_" + token;
            // send add executeQuery
            String updateQuery = "UPDATE " + updateTable +
                    " SET wellName = '" + updatedNote.getWellname() +
                    "', dateStart = '" + updateStartDate +
                    "', timeStart = '" + updatedStartTime +
                    "', mileageStart = '" + updatedNote.getMileageStart() +
                    "', description = '" + updatedNote.getDescription() +
                    "', mileageEnd = '" + updatedNote.getMileageEnd() +
                    "', dateEnd = '" + updatedEndTime +
                    "', timeEnd = '" + updatedEndDate +
                    "', projectNumber = '" + updatedNote.getProject() +
                    "', location = '" + updatedNote.getLocation() +
                    "', gps = '" + gpsString +
                    "', billing = '" + updatedNote.getBillingType() +
                    "' WHERE ticketNumber = '" + ticketNumber + "'";

            int numUpdatedRows = mMySqlService.executeUpdate(connection, updateQuery);

            String message;
            if (numUpdatedRows > 0) {
                // build results (using ticket number of added FieldNote)
                message = "Update Successful";

                // build results (using ticketNumber)
                ArrayList<Map<String, Object>> results = new ArrayList<>();
                Map<String, Object> result = new HashMap<>();
                result.put(TICKET_NUMBER_KEY, String.valueOf(ticketNumber));
                results.add(result);

                responseBodyBuilder.setResponseType(DHDPResponseType.SUCCESS);
                responseBodyBuilder.setMessage(message);
                responseBodyBuilder.setResults(results);
                mMySqlService.closeConnection(connection);
                return responseBodyBuilder.build();
            }

            message = "Update Failed";
            mLogger.warning(message);

            responseBodyBuilder.setResponseType(DHDPResponseType.FAILURE);
            responseBodyBuilder.setMessage(message);
            return responseBodyBuilder.build();
        } else {
            return malformedNote("No User Token");
        }
    }

    @Override
    public DHDPResponseBody deleteNote(String token, int ticketNumber) {
        if (token != null) {
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
                message = "Ticket does not exist";
            }

            // build results (number of tickets deleted)
            ArrayList<Map<String, Object>> results = new ArrayList<>();
            Map<String, Object> result = new HashMap<>();
            result.put(NUMBER_AFFECTED_ROWS_KEY, String.valueOf(numDeletedRows));
            results.add(result);

            responseBodyBuilder.setResponseType(DHDPResponseType.SUCCESS);
            responseBodyBuilder.setMessage(message);
            responseBodyBuilder.setResults(results);
            mMySqlService.closeConnection(connection);
            return responseBodyBuilder.build();
        } else {
            return malformedNote("No User Token");
        }
    }

    @Override
    public DHDPResponseBody searchNote(String token, Map<String, Object> searchParameters) {
        DHDPResponseBody.Builder responseBodyBuilder = DHDPResponseBody.newBuilder();

        String searchTable = "Data_" + token;
        String searchQuery = mQueryService.buildSearchQuery(searchTable, searchParameters);

        // make connection to database
        Connection connection = mMySqlService.getAwsConnection(DB_SERVER, DB_PORT, DB_DATABASE, DB_USERNAME, DB_PASSWORD);
        if (connection == null) {
            String message = "No Connection";
            mLogger.info(message);

            responseBodyBuilder.setResponseType(DHDPResponseType.FAILURE);
            responseBodyBuilder.setMessage(message);
            return responseBodyBuilder.build();
        }

        ResultSet resultSet = mMySqlService.executeQuery(connection, searchQuery);

        try {
            // build results (search results - as fieldnotes)
            ArrayList<Map<String, Object>> results = new ArrayList<>();
            Map<String, Object> result = new HashMap<>();

            while (resultSet.next()) {
                // create FieldNote from search results
                String dateStartString = resultSet.getString(DATE_START_COLUMN);
                String timeStartString = resultSet.getString(TIME_START_COLUMN);
                mLogger.info(dateStartString + "T" + timeStartString);
                LocalDateTime startTimeStamp = LocalDateTime.parse(dateStartString + "T" + timeStartString);

                //TODO: somehow the time and date columns are reversed.... look into this
//                String dateEndString = resultSet.getString(DATE_END_COLUMN);
//                String timeEndString = resultSet.getString(TIME_END_COLUMN);
                String dateEndString = resultSet.getString(TIME_END_COLUMN);
                String timeEndString = resultSet.getString(DATE_END_COLUMN);
                mLogger.info(dateEndString + "T" + timeEndString);
                LocalDateTime endTimeStamp = LocalDateTime.parse(dateEndString + "T" + timeEndString);

                GpsCoord gps = null;
                String gpsString = resultSet.getString(GPS_COLUMN);
                if (!gpsString.equals("Not Provided")) {
                    gps = GpsCoord.newBuilder()
                            .setLatitude(Double.parseDouble(gpsString.substring(0, gpsString.indexOf(","))))
                            .setLongitude(Double.parseDouble(gpsString.substring(gpsString.indexOf("," + 2))))
                            .build();
                }

                result.put(resultSet.getString(TICKET_NUMBER_COLUMN), FieldNote.newBuilder()
                        .setProject(resultSet.getString(PROJECT_NUMBER_COLUMN))
                        .setWellname(resultSet.getString(WELLNAME_COLUMN))
                        .setLocation(resultSet.getString(LOCATION_COLUMN))
                        .setBillingType(resultSet.getString(BILLING_COLUMN))
                        .setStartTimestamp(startTimeStamp)
                        .setEndTimestamp(endTimeStamp)
                        .setMileageStart(Integer.parseInt(resultSet.getString(MILEAGE_START_COLUMN)))
                        .setMileageEnd(Integer.parseInt(resultSet.getString(MILEAGE_END_COLUMN)))
                        .setDescription(resultSet.getString(DESCRIPTION_COLUMN))
                        .setGPSCoords(gps)
                        .build());

                results.add(result);
            }
            String message = "Search Successful";

            responseBodyBuilder.setResponseType(DHDPResponseType.SUCCESS);
            responseBodyBuilder.setMessage(message);
            responseBodyBuilder.setResults(results);
            mMySqlService.closeConnection(connection);
            return responseBodyBuilder.build();

        } catch (SQLException e) {
            mLogger.severe(e.toString());
            mMySqlService.closeConnection(connection);
        }

        String message = "Search Failed";
        mLogger.warning(message);

        responseBodyBuilder.setResponseType(DHDPResponseType.FAILURE);
        responseBodyBuilder.setMessage(message);
        return responseBodyBuilder.build();
    }

    private FieldNote searchNote(Connection connection, String token, int ticketNumber) throws Exception {
        String searchTable = "Data_" + token;
        String deleteQuery = "SELECT * FROM " + searchTable + " WHERE ticketNumber = '" + ticketNumber + "'";
        // send search executeQuery
        ResultSet resultSet = mMySqlService.executeQuery(connection, deleteQuery);

        try {
            if (resultSet != null && resultSet.next()) {
                // create FieldNote from search results
                String dateStartString = resultSet.getString(DATE_START_COLUMN);
                String timeStartString = resultSet.getString(TIME_START_COLUMN);
                LocalDateTime startTimeStamp = LocalDateTime.parse(dateStartString + "T" + timeStartString);

                String dateEndString = resultSet.getString(DATE_END_COLUMN);
                String timeEndString = resultSet.getString(TIME_END_COLUMN);
                LocalDateTime endTimeStamp = LocalDateTime.parse(dateEndString + "T" + timeEndString);

                GpsCoord gps = null;
                String gpsString = resultSet.getString(GPS_COLUMN);
                if (!gpsString.equals("Not Provided")) {
                    gps = GpsCoord.newBuilder()
                            .setLatitude(Double.parseDouble(gpsString.substring(0, gpsString.indexOf(","))))
                            .setLongitude(Double.parseDouble(gpsString.substring(gpsString.indexOf("," + 2))))
                            .build();
                }

                return FieldNote.newBuilder()
                        .setProject(resultSet.getString(PROJECT_NUMBER_COLUMN))
                        .setWellname(resultSet.getString(WELLNAME_COLUMN))
                        .setLocation(resultSet.getString(LOCATION_COLUMN))
                        .setBillingType(resultSet.getString(BILLING_COLUMN))
                        .setStartTimestamp(startTimeStamp)
                        .setEndTimestamp(endTimeStamp)
                        .setMileageStart(Integer.parseInt(resultSet.getString(MILEAGE_START_COLUMN)))
                        .setMileageEnd(Integer.parseInt(resultSet.getString(MILEAGE_END_COLUMN)))
                        .setDescription(resultSet.getString(DESCRIPTION_COLUMN))
                        .setGPSCoords(gps).build();
            }
        } catch (SQLException e) {
            mLogger.severe(e.toString());
            throw new Exception("Update Fetch Parse Failure");
        }
        throw new Exception("Update Fetch Failed");
    }

    @Override
    public DHDPResponseBody unsupportedNote(DHDPHeader header) {
        DHDPResponseBody.Builder response = DHDPResponseBody.newBuilder();
        response.setResponseType(DHDPResponseType.FAILURE);
        response.setMessage("Unsupported RequestType: " + header.getRequestType());
        response.setResults(null);
        return response.build();
    }

    @Override
    public DHDPResponseBody malformedNote(String message) {
        DHDPResponseBody.Builder response = DHDPResponseBody.newBuilder();
        response.setResponseType(DHDPResponseType.FAILURE);
        response.setMessage("Malformed Request: " + message);
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
    private ArrayList<Map<String, Object>> getResults(DHDPRequestType requestType, ResultSet resultSet)
            throws SQLException {
        ArrayList<Map<String, Object>> results = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();
        //build results from result set
        if (requestType.equals(DHDPRequestType.LOGIN)) {
            // result is login token
            resultMap.put(TOKEN_KEY, resultSet.getString(TOKEN_COLUMN));
        } else if (requestType.equals(DHDPRequestType.ADD)) {
            // result is add ticket number
            resultMap.put(TICKET_NUMBER_KEY, resultSet.getString(NEW_TICKET_KEY));
        }

        results.add(resultMap);
        return results;
    }

    private FieldNote mergeFieldNotes(FieldNote original, FieldNote update) {
        FieldNote.Builder updatedNote = FieldNote.newBuilder();
        // get updated project
        if (update.getProject() != null) {
            updatedNote.setProject(update.getProject());
        } else {
            updatedNote.setProject(original.getProject());
        }

        // get updated wellname
        if (update.getWellname() != null) {
            updatedNote.setWellname(update.getWellname());
        } else {
            updatedNote.setWellname(original.getWellname());
        }

        // get updated location
        if (update.getLocation() != null) {
            updatedNote.setLocation(update.getLocation());
        } else {
            updatedNote.setLocation(original.getLocation());
        }

        // get updated billing type
        if (update.getBillingType() != null) {
            updatedNote.setBillingType(update.getBillingType());
        } else {
            updatedNote.setBillingType(original.getBillingType());
        }

        // get updated start time
        if (update.getStartTimestamp() != null) {
            updatedNote.setStartTimestamp(update.getStartTimestamp());
        } else {
            updatedNote.setStartTimestamp(original.getStartTimestamp());
        }

        // get updated end time
        if (update.getEndTimestamp() != null) {
            updatedNote.setEndTimestamp(update.getEndTimestamp());
        } else {
            updatedNote.setEndTimestamp(original.getEndTimestamp());
        }

        // get updated start mileage
        // mileage of 0 is so unlikely - it's negligible
        if (update.getMileageStart() != 0) {
            updatedNote.setMileageStart(update.getMileageStart());
        } else {
            updatedNote.setMileageStart(original.getMileageStart());
        }

        // get updated end mileage
        // mileage of 0 is so unlikely - it's negligible
        if (update.getMileageEnd() != 0) {
            updatedNote.setMileageEnd(update.getMileageEnd());
        } else {
            updatedNote.setMileageEnd(original.getMileageEnd());
        }

        // get updated description
        if (update.getDescription() != null) {
            updatedNote.setDescription(update.getDescription());
        } else {
            updatedNote.setDescription(original.getDescription());
        }

        if (update.getGps() != null) {
            updatedNote.setGPSCoords(update.getGps());
        } else {
            updatedNote.setGPSCoords(original.getGps());
        }

        return updatedNote.build();
    }
}
