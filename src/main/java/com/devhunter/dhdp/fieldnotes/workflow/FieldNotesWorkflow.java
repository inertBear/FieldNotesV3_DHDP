package com.devhunter.dhdp.fieldnotes.workflow;

import com.devhunter.DHDPConnector4J.header.DHDPHeader;
import com.devhunter.DHDPConnector4J.request.DHDPRequest;
import com.devhunter.DHDPConnector4J.request.DHDPRequestBody;
import com.devhunter.DHDPConnector4J.response.DHDPResponseBody;
import com.devhunter.dhdp.fieldnotes.model.FieldNote;
import com.devhunter.dhdp.fieldnotes.service.FieldNoteService;
import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;
import com.devhunter.dhdp.infrastructure.DHDPWorkflow;

import java.util.ArrayList;
import java.util.List;

import static com.devhunter.dhdp.fieldnotes.FieldNotesConstants.*;

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
                return mService.login(body.getString(USERNAME_KEY), body.getString(PASSWORD_KEY));
            case ADD:
                // create FieldNote to add
                FieldNote fieldNote = FieldNote.newBuilder()
                        .setUsername(body.getString(USERNAME_KEY))
                        .setProject(body.getString(PROJECT_KEY))
                        .setWellname(body.getString(WELLNAME_KEY))
                        .setLocation(body.getString(LOCATION_KEY))
                        .setBillingType(body.getString(BILLING_KEY))
                        .setStartTimestamp(body.getLDT(START_DATETIME_KEY))
                        .setEndTimestamp(body.getLDT(END_DATETIME_KEY))
                        .setMileageStart(body.getInt(START_MILEAGE_KEY))
                        .setMileageEnd(body.getInt(END_MILEAGE_KEY))
                        .setDescription(body.getString(DESCRIPTION))
                        .setGPSCoords(body.getGpsCoord(GPS))
                        .build();

                return mService.addNote(body.getString(TOKEN_KEY), fieldNote);
            case DELETE:
                return mService.deleteNote(body.getString(TOKEN_KEY), body.getInt(TICKET_NUMBER_KEY));
            case SEARCH:
                // get search params
                List<Object> searchParams = new ArrayList<>();

                return mService.searchNote(body.getString(TOKEN_KEY), searchParams);
            case UPDATE:
                // create fieldnote to update to
                fieldNote = FieldNote.newBuilder()
                        .setUsername(body.getString(USERNAME_KEY))
                        .setProject(body.getString(PROJECT_KEY))
                        .setWellname(body.getString(WELLNAME_KEY))
                        .setLocation(body.getString(LOCATION_KEY))
                        .setBillingType(body.getString(BILLING_KEY))
                        .setStartTimestamp(body.getLDT(START_DATETIME_KEY))
                        .setEndTimestamp(body.getLDT(END_DATETIME_KEY))
                        .setMileageStart(body.getInt(START_MILEAGE_KEY))
                        .setMileageEnd(body.getInt(END_MILEAGE_KEY))
                        .setDescription(body.getString(DESCRIPTION))
                        .setGPSCoords(body.getGpsCoord(GPS))
                        .build();

                return mService.updateNote(body.getString(TOKEN_KEY),
                        body.getInt(TICKET_NUMBER_KEY), fieldNote);
            case LOGOUT:
            case REGISTER:
            default:
                return mService.unsupportedNote(requestHeader);
        }
    }
}
