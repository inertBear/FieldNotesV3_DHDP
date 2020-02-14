package com.devhunter.dhdp.fieldnotes.service;

import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;
import com.devhunter.dhdp.services.MySqlService;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.devhunter.dhdp.fieldnotes.FieldNotesConstants.*;
import static junit.framework.TestCase.assertEquals;

public class FieldNoteQueryServiceTest {
    private FieldNoteQueryService mService;

    @Before
    public void setup() {
        DHDPServiceRegistry registry = new DHDPServiceRegistry();
        FieldNoteTimeService.initService(registry);
        MySqlService.initService(registry);
        FieldNoteQueryService.initService(registry);
        mService = registry.resolve(FieldNoteQueryService.class);
    }

    @Test
    public void testBuildSearchQuery() {
        String token = "123456789";
        String expectedQuery = "SELECT * FROM Data_123456789 WHERE " +
                USERNAME_COLUMN + " = 'UNIT TEST' " +
                "AND " + TICKET_NUMBER_COLUMN + " = '1234' " +
                "AND " + PROJECT_NUMBER_COLUMN + " = 'PROJECT' " +
                "AND " + WELLNAME_COLUMN + " = 'WELLNAME' " +
                "AND " + LOCATION_COLUMN + " = 'LOCATION' " +
                "AND " + BILLING_COLUMN + " = 'BILLING' " +
                "AND " + DATE_START_COLUMN + " >= '2019-01-01' " +
                "AND " + DATE_END_COLUMN + " <= '2019-01-31'";

        Map<String, Object> params = new LinkedHashMap<>();
        params.put(TOKEN_KEY, token);
        params.put(USERNAME_KEY, "UNIT TEST");
        params.put(TICKET_NUMBER_KEY, "1234");
        params.put(PROJECT_KEY, "PROJECT");
        params.put(WELLNAME_KEY, "WELLNAME");
        params.put(LOCATION_KEY, "LOCATION");
        params.put(BILLING_KEY, "BILLING");
        params.put(START_DATETIME_KEY, "2019-01-01");
        params.put(END_DATETIME_KEY, "2019-01-31");

        String actualQuery = mService.buildSearchQuery(token, params);

        assertEquals(expectedQuery, actualQuery);
    }
}