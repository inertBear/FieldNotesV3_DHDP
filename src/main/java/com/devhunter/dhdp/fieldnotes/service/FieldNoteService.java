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
    private FieldNoteTimeService mTimeService;

    private FieldNoteService(final String name, final DHDPServiceRegistry registry) {
        super(name);
        mMySqlService = registry.resolve(MySqlService.class);
        mValidationService = registry.resolve(FieldNoteValidationService.class);
        mQueryService = registry.resolve(FieldNoteQueryService.class);
        mTimeService = registry.resolve(FieldNoteTimeService.class);
    }

    public static void initService(DHDPServiceRegistry registry) {
        if (!registry.containsService(FieldNoteService.class)) {
            registry.register(FieldNoteService.class, new FieldNoteService(FIELDNOTES_SERVICE_NAME, registry));
        }
    }

    @Override
    public DHDPResponseBody login(final String username, final String password) {
        DHDPResponseBody.Builder responseBodyBuilder = DHDPResponseBody.newBuilder();

        try {
            mValidationService.validateLogin(username, password);
        } catch (IllegalArgumentException e) {
            mLogger.warning("Failed to login: " + e.getMessage());
            responseBodyBuilder.setResponseType(DHDPResponseType.FAILURE);
            responseBodyBuilder.setMessage(e.getMessage());
            return responseBodyBuilder.build();
        }

        Connection connection = mQueryService.getDatabaseConnection();
        //make connection to database
        if (connection == null) {
            String message = "No Connection";
            mLogger.info(message);

            responseBodyBuilder.setResponseType(DHDPResponseType.FAILURE);
            responseBodyBuilder.setMessage(message);
            return responseBodyBuilder.build();
        }

        // build login query
        String loginQuery = mQueryService.buildLoginQuery(username, password);
        // executeQuery
        ResultSet resultSet = mMySqlService.executeQuery(connection, loginQuery);

        //process results
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
    public DHDPResponseBody addNote(final String token, final FieldNote fieldNote) {
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

            // make connection to database
            Connection connection = mQueryService.getDatabaseConnection();
            if (connection == null) {
                String message = "No Connection";
                mLogger.info(message);

                responseBodyBuilder.setResponseType(DHDPResponseType.FAILURE);
                responseBodyBuilder.setMessage(message);
                return responseBodyBuilder.build();
            }

            // build query
            String addQuery = mQueryService.buildAddQuery(token, fieldNote);
            // run query
            int numAlteredRows = mMySqlService.executeUpdate(connection, addQuery);

            // process add results
            if (numAlteredRows > 0) {
                String message = "FieldNote Added";

                // get the newly added ticket number
                ResultSet resultSet = mMySqlService.getLastInsertId(connection);

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
    public DHDPResponseBody updateNote(final String token, final int ticketNumber, FieldNote update) {
        if (token != null) {
            DHDPResponseBody.Builder responseBodyBuilder = DHDPResponseBody.newBuilder();

            // make connection to database
            Connection connection = mQueryService.getDatabaseConnection();
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
            final FieldNote updatedNote = mergeFieldNotes(original, update);

            // build query
            String updateQuery = mQueryService.buildUpdateQuery(token, ticketNumber, updatedNote);
            // run query
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
    public DHDPResponseBody deleteNote(final String token, final int ticketNumber) {
        if (token != null) {
            DHDPResponseBody.Builder responseBodyBuilder = DHDPResponseBody.newBuilder();

            // make connection to database
            Connection connection = mQueryService.getDatabaseConnection();
            if (connection == null) {
                String message = "No Connection";
                mLogger.info(message);

                responseBodyBuilder.setResponseType(DHDPResponseType.FAILURE);
                responseBodyBuilder.setMessage(message);
                return responseBodyBuilder.build();
            }

            //build query
            String deleteQuery = mQueryService.buildDeleteQuery(token, ticketNumber);
            // execute query
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
    public DHDPResponseBody searchNote(final String token, final Map<String, Object> searchParameters) {
        DHDPResponseBody.Builder responseBodyBuilder = DHDPResponseBody.newBuilder();

        // make connection to database
        Connection connection = mQueryService.getDatabaseConnection();
        if (connection == null) {
            String message = "No Connection";
            mLogger.info(message);

            responseBodyBuilder.setResponseType(DHDPResponseType.FAILURE);
            responseBodyBuilder.setMessage(message);
            return responseBodyBuilder.build();
        }

        // build query
        String searchQuery = mQueryService.buildSearchQuery(token, searchParameters);
        // execute query
        ResultSet resultSet = mMySqlService.executeQuery(connection, searchQuery);

        try {
            // build results (search results - as fieldnotes)
            ArrayList<Map<String, Object>> results = new ArrayList<>();
            Map<String, Object> result = new HashMap<>();

            while (resultSet.next()) {
                String dateStartString = resultSet.getString(DATE_START_COLUMN);
                String timeStartString = resultSet.getString(TIME_START_COLUMN);

                String dateEndString = resultSet.getString(DATE_END_COLUMN);
                String timeEndString = resultSet.getString(TIME_END_COLUMN);

                long startTimestampMillis = mTimeService.getDateInMillis(dateStartString + " " + timeStartString);
                long endTimestampMillis = mTimeService.getDateInMillis(dateEndString + " " + timeEndString);

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
                        .setStartTimeStampMillis(startTimestampMillis)
                        .setEndTimestampMillis(endTimestampMillis)
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

    private FieldNote searchNote(Connection connection, final String token, final int ticketNumber) throws Exception {
        // create query
        String searchQuery = mQueryService.buildSearchQuery(token, ticketNumber);
        // execute query
        ResultSet resultSet = mMySqlService.executeQuery(connection, searchQuery);

        try {
            if (resultSet != null && resultSet.next()) {
                String dateStartString = resultSet.getString(DATE_START_COLUMN);
                String timeStartString = resultSet.getString(TIME_START_COLUMN);

                String dateEndString = resultSet.getString(DATE_END_COLUMN);
                String timeEndString = resultSet.getString(TIME_END_COLUMN);

                long startTimestampMillis = mTimeService.getDateInMillis(dateStartString + " " + timeStartString);
                long endTimestampMillis = mTimeService.getDateInMillis(dateEndString + " " + timeEndString);

                return FieldNote.newBuilder()
                        .setProject(resultSet.getString(PROJECT_NUMBER_COLUMN))
                        .setWellname(resultSet.getString(WELLNAME_COLUMN))
                        .setLocation(resultSet.getString(LOCATION_COLUMN))
                        .setBillingType(resultSet.getString(BILLING_COLUMN))
                        .setStartTimeStampMillis(startTimestampMillis)
                        .setEndTimestampMillis(endTimestampMillis)
                        .setMileageStart(Integer.parseInt(resultSet.getString(MILEAGE_START_COLUMN)))
                        .setMileageEnd(Integer.parseInt(resultSet.getString(MILEAGE_END_COLUMN)))
                        .setDescription(resultSet.getString(DESCRIPTION_COLUMN))
                        .setGPSCoords(getGpsCoord(resultSet.getString(GPS_COLUMN)))
                        .build();
            }
        } catch (SQLException e) {
            mLogger.severe(e.toString());
            throw new Exception("Update Fetch Parse Failure");
        }
        throw new Exception("Update Fetch Failed");
    }

    @Override
    public DHDPResponseBody unsupportedNote(final DHDPHeader header) {
        DHDPResponseBody.Builder response = DHDPResponseBody.newBuilder();
        response.setResponseType(DHDPResponseType.FAILURE);
        response.setMessage("Unsupported RequestType: " + header.getRequestType());
        response.setResults(null);
        return response.build();
    }

    @Override
    public DHDPResponseBody malformedNote(final String message) {
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
    private ArrayList<Map<String, Object>> getResults(final DHDPRequestType requestType, final ResultSet resultSet)
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

    private GpsCoord getGpsCoord(final String gpsString) {
        if (gpsString != null) {
            if (!gpsString.equals("Not Provided")) {
                return GpsCoord.newBuilder()
                        .setLatitude(Double.parseDouble(gpsString.substring(0, gpsString.indexOf(","))))
                        .setLongitude(Double.parseDouble(gpsString.substring(gpsString.indexOf("," + 2))))
                        .build();
            }
        }
        return null;
    }

    private FieldNote mergeFieldNotes(final FieldNote original, final FieldNote update) {
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

        // NOTE: timestamps are required
        updatedNote.setStartTimeStampMillis(update.getStartTimestampMillis());
        updatedNote.setEndTimestampMillis(update.getEndTimestampMillis());

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
