package com.devhunter.dhdp.fieldnotes.service;

import com.devhunter.dhdp.infrastructure.DHDPService;
import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;

import java.util.Map;

import static com.devhunter.dhdp.fieldnotes.FieldNotesConstants.*;

public class FieldNoteQueryService extends DHDPService {

    private FieldNoteQueryService(String name) {
        super(name);
    }

    public static void initService(DHDPServiceRegistry registry) {
        if (!registry.containsService(FieldNoteQueryService.class)) {
            registry.register(FieldNoteQueryService.class, new FieldNoteQueryService(FIELDNOTES_QUERY_SERVICE_NAME));
        }
    }

    String buildSearchQuery(String tableName, Map<String, Object> searchParameters) {
        StringBuilder searchQuery = new StringBuilder("SELECT * FROM " + tableName);

        // take the token out of the params
        searchParameters.remove(TOKEN_KEY);

        // add each parameter to the query
        if (!searchParameters.isEmpty()) {

            boolean first = true;
            for (String each : searchParameters.keySet()) {
                if (first) {
                    // include a WHERE clause
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
