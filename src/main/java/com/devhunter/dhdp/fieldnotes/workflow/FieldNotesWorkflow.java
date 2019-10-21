package com.devhunter.dhdp.fieldnotes.workflow;

import com.devhunter.DHDPConnector4J.*;
import com.devhunter.DHDPConnector4J.groups.DHDPEntity;
import com.devhunter.DHDPConnector4J.groups.DHDPOrganization;
import com.devhunter.dhdp.fieldnotes.model.FieldNoteResponse;
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
    public DHDPResponse process(DHDPRequest request) {
        FieldNoteResponse fieldNote;
        DHDPHeader requestHeader = request.getHeader();
        DHDPBody body = request.getBody();

        // determine what the request wanted to do
        DHDPRequestType requestType = requestHeader.getRequestType();
        switch (requestType) {
            case LOGIN:
                fieldNote = mService.login(body);
                break;
            case ADD:
                fieldNote = mService.addNote(body);
                break;
            case DELETE:
                fieldNote = mService.deleteNote(body);
                break;
            case SEARCH:
                fieldNote = mService.searchNote(body);
                break;
            case UPDATE:
                fieldNote = mService.updateNote(body);
                break;
            case LOGOUT:
            case REGISTER:
            default:
                fieldNote = mService.unsupportedNote(body);
                break;
        }

        // build Response
        return DHDPResponse.newBuilder()
                .setHeader(DHDPHeader.newBuilder()
                        .setCreator(requestHeader.getString(DHDPHeader.CREATOR_KEY))
                        .setRequestType(requestHeader.getEnum(DHDPRequestType.class, DHDPHeader.REQUEST_TYPE_KEY))
                        .setOrganization(requestHeader.getEnum(DHDPOrganization.class, DHDPHeader.ORGANIZATION_KEY))
                        .setOriginator(requestHeader.getEnum(DHDPEntity.class, DHDPHeader.RECIPIENT_KEY))
                        .setRecipient(requestHeader.getEnum(DHDPEntity.class, DHDPHeader.ORIGINATOR_KEY))
                        .build())
                .setStatus(fieldNote.getStatus())
                .setMessage(fieldNote.getMessage())
                .setResults(fieldNote.getResults())
                .setTimestamp(fieldNote.getTimestamp())
                .build();
    }
}
