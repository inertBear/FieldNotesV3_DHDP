package com.devhunter.dhdp.fieldnotes.workflow;

import com.devhunter.DHDPConnector4J.header.DHDPHeader;
import com.devhunter.DHDPConnector4J.request.DHDPRequest;
import com.devhunter.DHDPConnector4J.request.DHDPRequestBody;
import com.devhunter.DHDPConnector4J.response.DHDPResponseBody;
import com.devhunter.dhdp.fieldnotes.model.FieldNote;
import com.devhunter.dhdp.fieldnotes.service.FieldNoteService;
import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;
import com.devhunter.dhdp.infrastructure.DHDPWorkflow;

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
        // get token
        String token = body.getString(TOKEN_KEY);

        // determine what the request wanted to do
        switch (requestHeader.getRequestType()) {
            case LOGIN:
                // get login credentials
                String username = body.getString(USERNAME_KEY);
                String password = body.getString(PASSWORD_KEY);

                return mService.login(username, password);
            case ADD:
                // create FieldNote to add
                FieldNote fieldNote = FieldNote.newBuilder()
                        .setUsername(body.getString(USERNAME_KEY))
                        .setProject(body.getString(PROJECT_KEY))
                        .setWellname(body.getString(WELLNAME_KEY))
                        .setLocation(body.getString(LOCATION_KEY))
                        .setBillingType(body.getString(BILLING_KEY))
                        .setDateStart(body.getLDT(START_DATETIME_KEY))
                        .setDateEnd(body.getLDT(END_DATETIME_KEY))
                        .setMileageStart(body.getInt(START_MILEAGE_KEY))
                        .setMileageEnd(body.getInt(END_MILEAGE_KEY))
                        .setDescription(body.getString(DESCRIPTION))
                        .setGPSCoords(body.getGpsCoord(GPS))
                        .build();

                return mService.addNote(token, fieldNote);
            case DELETE:
                // get ticket number to delete
                int ticketNumber = body.getInt(TICKET_NUMBER_KEY);

                return mService.deleteNote(token, ticketNumber);
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
