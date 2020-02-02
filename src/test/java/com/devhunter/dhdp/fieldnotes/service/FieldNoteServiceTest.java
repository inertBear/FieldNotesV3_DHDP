package com.devhunter.dhdp.fieldnotes.service;

import com.devhunter.DHDPConnector4J.model.GpsCoord;
import com.devhunter.DHDPConnector4J.response.DHDPResponse;
import com.devhunter.DHDPConnector4J.response.DHDPResponseBody;
import com.devhunter.DHDPConnector4J.response.DHDPResponseType;
import com.devhunter.dhdp.fieldnotes.model.FieldNote;
import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;
import com.devhunter.dhdp.services.MySqlService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDateTime;

import static com.devhunter.dhdp.fieldnotes.FieldNotesConstants.*;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FieldNoteServiceTest {
    private FieldNoteService mFieldNoteService;

    @Before
    public void setup() {
        DHDPServiceRegistry registry = new DHDPServiceRegistry();
        MySqlService.initService(registry);
        FieldNoteValidationService.initService(registry);
        FieldNoteService.initService(registry);
        mFieldNoteService = registry.resolve(FieldNoteService.class);
    }

    /**
     * Test FieldNotes Service login
     */
    @Test
    public void testLogin() {
        // use the FNService to login
        DHDPResponseBody response = mFieldNoteService.login("keithh", "hunterk");

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
                .setDateStart(LocalDateTime.now())
                .setDateEnd(LocalDateTime.now())
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
        response = mFieldNoteService.deleteNote(token, Integer.parseInt(response.getResults().get(0).get(TICKET_NUMBER_KEY)));
        assertEquals(DHDPResponseType.SUCCESS, response.getResponseType());
        assertEquals("Delete Successful", response.getMessage());
        assertEquals("1", response.getResults().get(0).get(NUMBER_AFFECTED_ROWS_KEY));
    }

    /**
     * Test FieldNotes service add note
     */
    @Test
    public void testAddAndDeleteNoteWithoutGps() {
        String token = "1159616266";
        // create new FieldNote
        FieldNote fn = FieldNote.newBuilder()
                .setUsername("Unit Test")
                .setProject("Test Project")
                .setWellname("Test WellName")
                .setLocation("Office")
                .setBillingType("Not Billable")
                .setDateStart(LocalDateTime.now())
                .setDateEnd(LocalDateTime.now())
                .setMileageStart(1)
                .setMileageEnd(2)
                .setDescription("Test Description")
                .build();

        // use the FNService to addNote
        DHDPResponseBody response = mFieldNoteService.addNote(token, fn);
        assertEquals(DHDPResponseType.SUCCESS, response.getResponseType());
        assertEquals("Add Successful", response.getMessage());
        assertNotNull(response.getResults().get(0).get(TICKET_NUMBER_KEY));

        // delete note added from unit test
        response = mFieldNoteService.deleteNote(token, Integer.parseInt(response.getResults().get(0).get(TICKET_NUMBER_KEY)));
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