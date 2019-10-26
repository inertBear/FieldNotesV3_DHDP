package com.devhunter.dhdp;

import com.devhunter.DHDPConnector4J.groups.DHDPEntity;
import com.devhunter.DHDPConnector4J.groups.DHDPOrganization;
import com.devhunter.DHDPConnector4J.header.DHDPHeader;
import com.devhunter.DHDPConnector4J.request.DHDPRequestType;
import com.devhunter.dhdp.fieldnotes.service.FieldNoteService;
import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;
import com.devhunter.dhdp.infrastructure.DHDPWorkflow;
import com.devhunter.dhdp.services.MySqlService;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DHDPWorkflowHandlerTest {

    /**
     * test that the workflow handler can get the correct workflow from originator
     */
    @Test
    public void testWorkflowHandler() {
        DHDPServiceRegistry registry = new DHDPServiceRegistry();
        MySqlService.initService(registry);
        FieldNoteService.initService(registry);
        DHDPWorkflowHandler handler = new DHDPWorkflowHandler(registry);

        DHDPHeader header = DHDPHeader.newBuilder()
                .setCreator("Unit Test")
                .setOrganization(DHDPOrganization.DEVHUNTER)
                .setOriginator(DHDPEntity.FieldNotes)
                .setRecipient(DHDPEntity.DHDP)
                .setRequestType(DHDPRequestType.LOGIN)
                .build();

        DHDPWorkflow workflow = handler.getWorkflow(header);
        assertEquals("FieldNotesWorkflow", workflow.getName());
    }
}