package com.devhunter.dhdp.fieldnotes.workflow;

import com.devhunter.DHDPConnector4J.*;
import com.devhunter.DHDPConnector4J.groups.DHDPEntity;
import com.devhunter.DHDPConnector4J.groups.DHDPOrganization;
import com.devhunter.dhdp.fieldnotes.service.FieldNoteService;
import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;
import com.devhunter.dhdp.services.MySqlService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.devhunter.DHDPConnector4J.constants.FieldNotesConstants.TOKEN_KEY;
import static com.devhunter.dhdp.fieldnotes.FieldNotesConstants.PASSWORD_TAG;
import static com.devhunter.dhdp.fieldnotes.FieldNotesConstants.USERNAME_TAG;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.fail;

public class FieldNotesWorkflowTest {
    private static final String TEST_CREATOR = "Unit Test";
    private FieldNotesWorkflow mWorkflow;

    @Before
    public void setup() {
        DHDPServiceRegistry registry = new DHDPServiceRegistry();
        MySqlService.initService(registry);
        FieldNoteService.initService(registry);
        mWorkflow = new FieldNotesWorkflow("FieldNotesWorkflow", registry);
    }

    /**
     * Test FieldNotes login workflow
     */
    @Test
    public void testLoginWorkflow() {
        final String TEST_USERNAME = "keithh";
        final String TEST_PASSWORD = "hunterk";
        final String TEST_TOKEN = "1159616266";
        final String LOGIN_SUCCESS_MESSAGE = "Login Successful";
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put(USERNAME_TAG, TEST_USERNAME);
        requestMap.put(PASSWORD_TAG, TEST_PASSWORD);

        //create Login request
        DHDPRequest request = DHDPRequest.newBuilder()
                .setHeader(DHDPHeader.newBuilder()
                        .setCreator(TEST_CREATOR)
                        .setOriginator(DHDPEntity.DHDPConnector)
                        .setRecipient(DHDPEntity.DHDP)
                        .setOrganization(DHDPOrganization.DEVHUNTER)
                        .setRequestType(DHDPRequestType.LOGIN)
                        .build())
                .setBody(new DHDPBody(requestMap))
                .build();

        //process request
        DHDPResponse response = mWorkflow.process(request);

        DHDPHeader header = response.getHeader();

        assertEquals(DHDPRequestType.LOGIN, header.getRequestType());
        assertEquals(TEST_CREATOR, header.getCreator());
        assertEquals(DHDPOrganization.DEVHUNTER, header.getOrganization());
        assertEquals(DHDPEntity.DHDP, header.getOriginator());
        assertEquals(DHDPEntity.DHDPConnector, header.getRecipient());

        assertEquals(DHDPResponseType.SUCCESS, response.getResponseType());
        assertEquals(LOGIN_SUCCESS_MESSAGE, response.getMessage());
        assertEquals(TEST_TOKEN, response.getResults().get(0).get(TOKEN_KEY));
    }

    /**
     * Test FieldNotes Add Note workflow
     */
    @Test
    @Ignore
    public void testAddWorkflow() {
        fail();
    }

    /**
     * test FieldNotes Delete Note Workflow
     */
    @Test
    @Ignore
    public void testDeleteWorkflow() {
        fail();
    }

    /**
     * test FieldNotes Search Note with a single result workflow
     */
    @Test
    @Ignore
    public void testSearchSingleResultWorkflow() {
        fail();
    }

    /**
     * test FieldNotes Search Note with multiple results workflow
     */
    @Test
    @Ignore
    public void testSearchMultipleResultsWorkflow() {
        fail();
    }

    /**
     * test FieldNotes Update Note Workflow
     */
    @Test
    @Ignore
    public void testUpdateWorkflow() {
        fail();
    }
}