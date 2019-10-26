package com.devhunter.dhdp.fieldnotes.workflow;

import com.devhunter.DHDPConnector4J.*;
import com.devhunter.DHDPConnector4J.header.DHDPHeader;
import com.devhunter.DHDPConnector4J.request.DHDPRequest;
import com.devhunter.DHDPConnector4J.request.DHDPRequestBody;
import com.devhunter.DHDPConnector4J.request.DHDPRequestType;
import com.devhunter.DHDPConnector4J.response.DHDPResponseBody;
import com.devhunter.dhdp.fieldnotes.service.FieldNoteService;
import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;
import com.devhunter.dhdp.infrastructure.DHDPWorkflow;

/**
 * defines the Workflow for FieldNotes.
 * <p>
 * A Request will contain a Header with a RequestType.
 * The RequestType determines the path the workflow
 * will take.
 */
public class FieldNotesWorkflow extends DHDPWorkflow {
    private FieldNoteService mService;

    public FieldNotesWorkflow(String name, DHDPServiceRegistry registry) {
        super(name, registry);
        mService = mRegistry.resolve(FieldNoteService.class);
    }

    @Override
    public DHDPResponseBody process(DHDPRequest request) {
        DHDPHeader requestHeader = request.getHeader();
        DHDPRequestBody body = request.getBody();

        // determine what the request wanted to do
        switch (requestHeader.getRequestType()) {
            case LOGIN:
                return mService.login(body);
            case ADD:
                return mService.addNote(body);
            case DELETE:
                return mService.deleteNote(body);
            case SEARCH:
                return mService.searchNote(body);
            case UPDATE:
                return mService.updateNote(body);
            case LOGOUT:
            case REGISTER:
            default:
                return mService.unsupportedNote(requestHeader);
        }
    }
}
