package com.devhunter.dhdp.fieldnotes.service;

import com.devhunter.DHDPConnector4J.model.GpsCoord;
import com.devhunter.DHDPConnector4J.response.DHDPResponseBody;
import com.devhunter.DHDPConnector4J.response.DHDPResponseType;
import com.devhunter.dhdp.fieldnotes.model.FieldNote;
import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;
import com.devhunter.dhdp.services.MySqlService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.devhunter.dhdp.fieldnotes.FieldNotesConstants.*;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FieldNoteServiceTest {
    private FieldNoteService mFieldNoteService;

    @Before
    public void setup() {
        DHDPServiceRegistry registry = new DHDPServiceRegistry();
        FieldNoteTimeService.initService(registry);
        MySqlService.initService(registry);
        FieldNoteValidationService.initService(registry);
        FieldNoteQueryService.initService(registry);
        FieldNoteService.initService(registry);
        mFieldNoteService = registry.resolve(FieldNoteService.class);
    }

    /**
     * Test FieldNotes Service login
     */
    @Test
    public void testLogin() {
        // use the FNService to login
        DHDPResponseBody response = mFieldNoteService.login("Unit Test", "fnunittest");

        // process a successful login
        assertEquals(DHDPResponseType.SUCCESS, response.getResponseType());
        assertEquals("Login Successful", response.getMessage());
        assertEquals("1159616266", response.getResults().get(0).get(TOKEN_KEY));
    }

    /**
     * Test FieldNotes service add note
     */
    @Test
    public void testAddAndDeleteNoteWithGps() {
        String token = "1159616266";
        // create new FieldNote
        FieldNote fn = FieldNote.newBuilder()
                .setUsername("Unit Test")
                .setProject("Test Project")
                .setWellname("Test WellName")
                .setLocation("Office")
                .setBillingType("Not Billable")
                .setStartTimeStampMillis(System.currentTimeMillis())
                .setEndTimestampMillis(System.currentTimeMillis())
                .setMileageStart(1)
                .setMileageEnd(2)
                .setDescription("Test Description")
                .setGPSCoords(GpsCoord.newBuilder()
                        .setLatitude(13.134)
                        .setLongitude(9.089)
                        .build())
                .build();

        // use the FNService to addNote
        DHDPResponseBody response = mFieldNoteService.addNote(token, fn);

        // process a successful add
        assertEquals(DHDPResponseType.SUCCESS, response.getResponseType());
        assertEquals("Add Successful", response.getMessage());
        assertNotNull(response.getResults().get(0).get(TICKET_NUMBER_KEY));

        // delete note added from unit test
        response = mFieldNoteService.deleteNote(token, Integer.parseInt(response.getResults().get(0).get(TICKET_NUMBER_KEY).toString()));
        assertEquals(DHDPResponseType.SUCCESS, response.getResponseType());
        assertEquals("Delete Successful", response.getMessage());
        assertEquals("1", response.getResults().get(0).get(NUMBER_AFFECTED_ROWS_KEY));
    }

    /**
     * Test FieldNotes service add note
     */
    @Test
    public void testAddUpdateSearchAndDeleteNoteWithoutGps() {
        String token = "1159616266";
        // create new FieldNote
        FieldNote fn = FieldNote.newBuilder()
                .setUsername("Unit Test")
                .setProject("Test Project")
                .setWellname("Test WellName")
                .setLocation("Office")
                .setBillingType("Not Billable")
                .setStartTimeStampMillis(System.currentTimeMillis())
                .setEndTimestampMillis(System.currentTimeMillis())
                .setMileageStart(1)
                .setMileageEnd(2)
                .setDescription("Test Description")
                .build();

        // ADD
        DHDPResponseBody response = mFieldNoteService.addNote(token, fn);
        assertEquals(DHDPResponseType.SUCCESS, response.getResponseType());
        assertEquals("Add Successful", response.getMessage());
        String addedTicketNumberString = response.getResults().get(0).get(TICKET_NUMBER_KEY).toString();
        assertNotNull(addedTicketNumberString);

        // get added ticket number
        int addedTicketNumber = Integer.parseInt(addedTicketNumberString);

        // UPDATE
        response = mFieldNoteService.updateNote(token, addedTicketNumber, fn);
        assertEquals(DHDPResponseType.SUCCESS, response.getResponseType());
        assertEquals("Update Successful", response.getMessage());
        String updatedTicketNumberString = response.getResults().get(0).get(TICKET_NUMBER_KEY).toString();
        assertNotNull(updatedTicketNumberString);

        // get updated ticket number
        int updatedTicketNumber = Integer.parseInt(addedTicketNumberString);
        assertEquals(addedTicketNumber, updatedTicketNumber);

        Map<String, Object> searchParams = new LinkedHashMap<>();
        searchParams.put(TICKET_NUMBER_KEY, updatedTicketNumber);

        // SEARCH (by ticketNumber)
        response = mFieldNoteService.searchNote(token, searchParams);
        assertEquals(DHDPResponseType.SUCCESS, response.getResponseType());
        assertEquals("Search Successful", response.getMessage());
        List<Map<String, Object>> results = response.getResults();
        // RX one result
        assertEquals(1, results.size());
        Map<String, Object> resultMap = results.get(0);
        // key is ticket number
        assertTrue(resultMap.containsKey(String.valueOf(updatedTicketNumber)));
        // value is fn
        assertEquals(fn, resultMap.get(String.valueOf(updatedTicketNumber)));

        // DELETE
        response = mFieldNoteService.deleteNote(token, updatedTicketNumber);
        assertEquals(DHDPResponseType.SUCCESS, response.getResponseType());
        assertEquals("Delete Successful", response.getMessage());
        assertEquals("1", response.getResults().get(0).get(NUMBER_AFFECTED_ROWS_KEY));
    }

    /**
     * Test FieldNotes service search note w/ single result
     */
    @Test
    @Ignore
    public void testSearchNoteSingleResult() {
        fail();
    }

    /**
     * Test FieldNotes service search not w/ multi results
     */
    @Test
    @Ignore
    public void testSeachNoteMultipleResults() {
        fail();
    }
}