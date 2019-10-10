package com.devhunter.dhdp.fieldnotes;

import com.devhunter.DHDPConnector4J.*;
import com.devhunter.dhdp.infrastructure.DHDPWorkflow;
import com.devhunter.dhdp.infrastructure.DHServiceRegistry;

public class FieldNotesWorkflow extends DHDPWorkflow {
    private FieldNoteService mService;

    public FieldNotesWorkflow(DHServiceRegistry registry) {
        super(registry);
        mService = mRegistry.resolve(FieldNoteService.class);
    }

    @Override
    public DHDPResponse process(DHDPRequest request) {
        FieldNote fieldNote = null;
        DHDPHeader requestHeader = request.getHeader();
        DHDPBody body = request.getBody();

        // determine what the request wanted to do and get response body
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
        }

        // build Response Header
        DHDPHeader header = DHDPHeader.newBuilder()
                .setCreator(requestHeader.getString(DHDPHeader.CREATOR_KEY))
                .setRequestType(requestHeader.getEnum(DHDPRequestType.class, DHDPHeader.REQUEST_TYPE_KEY))
                .setOrganization(requestHeader.getEnum(DHDPOrganization.class, DHDPHeader.ORGANIZATION_KEY))
                .setOriginator(requestHeader.getEnum(DHDPEntity.class, DHDPHeader.RECIPIENT_KEY))
                .setRecipient(requestHeader.getEnum(DHDPEntity.class, DHDPHeader.ORIGINATOR_KEY))
                .build();

        return DHDPResponse.newBuilder()
                .setHeader(header)
                .setBody(fieldNote)
                .build();
    }
}
