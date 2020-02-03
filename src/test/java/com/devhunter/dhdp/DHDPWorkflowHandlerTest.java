package com.devhunter.dhdp;

import com.devhunter.DHDPConnector4J.header.DHDPHeader;
import com.devhunter.DHDPConnector4J.request.DHDPRequestType;
import com.devhunter.dhdp.fieldnotes.service.FieldNoteQueryService;
import com.devhunter.dhdp.fieldnotes.service.FieldNoteService;
import com.devhunter.dhdp.fieldnotes.service.FieldNoteValidationService;
import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;
import com.devhunter.dhdp.infrastructure.DHDPWorkflow;
import com.devhunter.dhdp.services.MySqlService;
import org.junit.Test;

import static com.devhunter.dhdp.dhdpconnector.DhdpConnectorConstants.UNIT_TEST_CREATOR;
import static com.devhunter.dhdp.dhdpconnector.DhdpConnectorConstants.UNIT_TEST_ORGANIZATION;
import static com.devhunter.dhdp.fieldnotes.FieldNotesConstants.FIELDNOTES_SERVICE_NAME;
import static com.devhunter.dhdp.fieldnotes.FieldNotesConstants.FIELDNOTES_WORKFLOW_NAME;
import static com.devhunter.dhdp.infrastructure.DHDPConstants.DHDP_NAME;
import static org.junit.Assert.assertEquals;

public class DHDPWorkflowHandlerTest {

    /**
     * test that the workflow handler can get the correct workflow from originator
     */
    @Test
    public void testWorkflowHandler() {
        DHDPServiceRegistry registry = new DHDPServiceRegistry();
        MySqlService.initService(registry);
        FieldNoteValidationService.initService(registry);
        FieldNoteQueryService.initService(registry);
        FieldNoteService.initService(registry);
        DHDPWorkflowHandler handler = new DHDPWorkflowHandler(registry);

        DHDPHeader header = DHDPHeader.newBuilder()
                .setCreator(UNIT_TEST_CREATOR)
                .setOrganization(UNIT_TEST_ORGANIZATION)
                .setOriginator(FIELDNOTES_SERVICE_NAME)
                .setRecipient(DHDP_NAME)
                .setRequestType(DHDPRequestType.LOGIN)
                .build();

        DHDPWorkflow workflow = handler.getWorkflow(header);
        assertEquals(FIELDNOTES_WORKFLOW_NAME, workflow.getName());
    }
}