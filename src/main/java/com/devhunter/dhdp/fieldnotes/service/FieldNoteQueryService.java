package com.devhunter.dhdp.fieldnotes.service;

import com.devhunter.dhdp.fieldnotes.model.FieldNote;
import com.devhunter.dhdp.infrastructure.DHDPService;
import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;
import com.devhunter.dhdp.services.MySqlService;

import java.sql.Connection;
import java.util.Map;

import static com.devhunter.dhdp.fieldnotes.FieldNotesConstants.*;

/**
 * Service to build query and take some of the load off of the FieldNoteService
 */
public class FieldNoteQueryService extends DHDPService {
    private MySqlService mMySqlService;
    private FieldNoteFormatService mFormatService;

    private FieldNoteQueryService(final String name, final DHDPServiceRegistry registry) {
        super(name);
        mMySqlService = registry.resolve(MySqlService.class);
        mFormatService = registry.resolve(FieldNoteFormatService.class);
    }

    public static void initService(DHDPServiceRegistry registry) {
        if (!registry.containsService(FieldNoteQueryService.class)) {
            registry.register(FieldNoteQueryService.class, new FieldNoteQueryService(FIELDNOTES_QUERY_SERVICE_NAME, registry));
        }
    }

    Connection getDatabaseConnection() {
        //make connection to database
        return mMySqlService.getAwsConnection(DB_SERVER, DB_PORT, DB_DATABASE, DB_USERNAME, DB_PASSWORD);
    }

    String buildLoginQuery(final String username, final String password) {
        return "SELECT " + TOKEN_COLUMN +
                " FROM " + LOGIN_TABLE +
                " WHERE " + USERNAME_COLUMN + " = '" + username + "'" +
                " AND " + PASSWORD_COLUMN + " = '" + password + "'";
    }

    String buildAddQuery(final String token, final FieldNote fieldNote) {
        // create tablename
        String tableName = "Data_" + token;

        // prepare timestamps
        String startDate = mFormatService.toDateString(fieldNote.getStartTimestampMillis());
        String startTime = mFormatService.toTimeString(fieldNote.getEndTimestampMillis());
        String endDate = mFormatService.toDateString(fieldNote.getEndTimestampMillis());
        String endTime = mFormatService.toTimeString(fieldNote.getEndTimestampMillis());

        // prepare gps : gps is not required
        String gpsString = mFormatService.toGpsString(fieldNote.getGps());

        // create add query
        return "INSERT INTO " + tableName +
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
                "'" + endDate + "'" + ", " +
                "'" + endTime + "'" + ", " +
                "'" + fieldNote.getProject() + "'" + ", " +
                "'" + fieldNote.getLocation() + "'" + ", " +
                "'" + gpsString + "'" + ", " +
                "'" + fieldNote.getBillingType() + "'" +
                ");";

    }

    String buildUpdateQuery(final String token, final int ticketNumber, final FieldNote fieldNote) {
        // create tablename
        String tableName = "Data_" + token;

        // prepare timestamps
        String startDate = mFormatService.toDateString(fieldNote.getStartTimestampMillis());
        String startTime = mFormatService.toTimeString(fieldNote.getEndTimestampMillis());
        String endDate = mFormatService.toDateString(fieldNote.getEndTimestampMillis());
        String endTime = mFormatService.toTimeString(fieldNote.getEndTimestampMillis());

        // prepare gps : gps is not required
        String gpsString = mFormatService.toGpsString(fieldNote.getGps());

        // create update query
        return "UPDATE " + tableName +
                " SET wellName = '" + fieldNote.getWellname() +
                "', dateStart = '" + startDate +
                "', timeStart = '" + startTime +
                "', mileageStart = '" + fieldNote.getMileageStart() +
                "', description = '" + fieldNote.getDescription() +
                "', mileageEnd = '" + fieldNote.getMileageEnd() +
                "', dateEnd = '" + endDate +
                "', timeEnd = '" + endTime +
                "', projectNumber = '" + fieldNote.getProject() +
                "', location = '" + fieldNote.getLocation() +
                "', gps = '" + gpsString +
                "', billing = '" + fieldNote.getBillingType() +
                "' WHERE ticketNumber = '" + ticketNumber + "'";
    }

    String buildDeleteQuery(final String token, final int ticketNumber) {
        String tableName = "Data_" + token;
        return "DELETE FROM " + tableName + " WHERE ticketNumber = '" + ticketNumber + "'";
    }

    String buildSearchQuery(final String token, final int ticketNumber) {
        String tableName = "Data_" + token;
        return "SELECT * FROM " + tableName + " WHERE ticketNumber = '" + ticketNumber + "'";
    }

    /**
     * build a search query based on a map of search parameters
     *
     * @param token
     * @param searchParameters
     * @return
     */
    String buildSearchQuery(final String token, Map<String, Object> searchParameters) {
        String tableName = "Data_" + token;
        StringBuilder searchQuery = new StringBuilder("SELECT * FROM " + tableName);

        // take the token out of the params (if its there)
        searchParameters.remove(TOKEN_KEY);

        // add each parameter to the query
        if (!searchParameters.isEmpty()) {

            boolean first = true;
            for (String each : searchParameters.keySet()) {
                if (first) {
                    // include a WHERE clause on the first iteration of the loop
                    searchQuery.append(" WHERE ");
                    first = false;
                } else {
                    searchQuery.append(" AND ");
                }

                switch (each) {
                    case TICKET_NUMBER_KEY:
                        searchQuery.append(TICKET_NUMBER_COLUMN).append(" = '").append(searchParameters.get(each)).append("'");
                        break;
                    case PROJECT_KEY:
                        searchQuery.append(PROJECT_NUMBER_COLUMN).append(" = '").append(searchParameters.get(each)).append("'");
                        break;
                    case WELLNAME_KEY:
                        searchQuery.append(WELLNAME_COLUMN).append(" = '").append(searchParameters.get(each)).append("'");
                        break;
                    case LOCATION_KEY:
                        searchQuery.append(LOCATION_COLUMN).append(" = '").append(searchParameters.get(each)).append("'");
                        break;
                    case BILLING_KEY:
                        searchQuery.append(BILLING_COLUMN).append(" = '").append(searchParameters.get(each)).append("'");
                        break;
                    case USERNAME_KEY:
                        searchQuery.append(USERNAME_COLUMN).append(" = '").append(searchParameters.get(each)).append("'");
                        break;
                    case START_DATETIME_KEY:
                        searchQuery.append(DATE_START_COLUMN).append(" >= '").append(searchParameters.get(each)).append("'");
                        break;
                    case END_DATETIME_KEY:
                        searchQuery.append(DATE_END_COLUMN).append(" <= '").append(searchParameters.get(each)).append("'");
                        break;
                    case DESCRIPTION_KEY:
                }
            }
        }
        return searchQuery.toString();
    }
}
