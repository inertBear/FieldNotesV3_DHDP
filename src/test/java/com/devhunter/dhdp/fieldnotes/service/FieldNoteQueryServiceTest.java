package com.devhunter.dhdp.fieldnotes.service;

import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.devhunter.dhdp.fieldnotes.FieldNotesConstants.*;
import static junit.framework.TestCase.assertEquals;

public class FieldNoteQueryServiceTest {
    private FieldNoteQueryService mService;

    @Before
    public void setup() {
        DHDPServiceRegistry registry = new DHDPServiceRegistry();
        FieldNoteQueryService.initService(registry);
        mService = registry.resolve(FieldNoteQueryService.class);
    }

    @Test
    public void testBuildSearchQuery() {
        String expectedQuery = "SELECT * FROM DataTable WHERE " +
                USERNAME_COLUMN + " = 'UNIT TEST' " +
                "AND " + TICKET_NUMBER_COLUMN + " = '1234' " +
                "AND " + PROJECT_NUMBER_COLUMN + " = 'PROJECT' " +
                "AND " + WELLNAME_COLUMN + " = 'WELLNAME' " +
                "AND " + LOCATION_COLUMN + " = 'LOCATION' " +
                "AND " + BILLING_COLUMN + " = 'BILLING' " +
                "AND " + DATE_START_COLUMN + " >= '2019-01-01' " +
                "AND " + DATE_END_COLUMN + " <= '2019-01-31'";

        String tableName = "DataTable";
        Map<String, Object> params = new LinkedHashMap<>();
        params.put(TOKEN_KEY, "123456789");
        params.put(USERNAME_KEY, "UNIT TEST");
        params.put(TICKET_NUMBER_KEY, "1234");
        params.put(PROJECT_KEY, "PROJECT");
        params.put(WELLNAME_KEY, "WELLNAME");
        params.put(LOCATION_KEY, "LOCATION");
        params.put(BILLING_KEY, "BILLING");
        params.put(START_DATETIME_KEY, "2019-01-01");
        params.put(END_DATETIME_KEY, "2019-01-31");

        String actualQuery = mService.buildSearchQuery(tableName, params);

        assertEquals(expectedQuery, actualQuery);
    }
}