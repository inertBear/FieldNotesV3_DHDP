package com.devhunter.dhdp.fieldnotes.workflow;

import com.devhunter.DHDPConnector4J.header.DHDPHeader;
import com.devhunter.DHDPConnector4J.model.GpsCoord;
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
 * 1) A Request will contain a Header with a RequestType.
 * 2) The RequestType determines the path the workflow will take.
 * 3) The DHDPRequestBody will be converted to a FieldNote and processed by the FieldNotesService
 * 4) Always returns a valid DHDPResponseBody
 */
public class FieldNotesWorkflow extends DHDPWorkflow {
    private FieldNoteService mService;

    public FieldNotesWorkflow(final String name, final DHDPServiceRegistry registry) {
        super(name, registry);
        mService = mRegistry.resolve(FieldNoteService.class);
    }

    @Override
    public DHDPResponseBody process(final DHDPRequest request) {
        DHDPHeader requestHeader = request.getHeader();
        DHDPRequestBody body = request.getBody();

        //TODO: validate here instead of in service calls
        try {
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
                            .setStartTimeStampMillis(Long.valueOf(body.getString(START_DATETIME_KEY)))
                            .setEndTimestampMillis(Long.valueOf(body.getString(END_DATETIME_KEY)))
                            .setMileageStart(Integer.parseInt(body.getString(START_MILEAGE_KEY)))
                            .setMileageEnd(Integer.parseInt(body.getString(END_MILEAGE_KEY)))
                            .setDescription(body.getString(DESCRIPTION_KEY))
                            .setGPSCoords(new GpsCoord(body.getString(GPS_KEY)))
                            .build();

                    return mService.addNote(body.getString(TOKEN_KEY), fieldNote);
                case DELETE:
                    return mService.deleteNote(body.getString(TOKEN_KEY), body.getInt(TICKET_NUMBER_KEY));
                case SEARCH:
                    return mService.searchNote(body.getString(TOKEN_KEY), body);
                case UPDATE:
                    // create fieldnote to update to
                    fieldNote = FieldNote.newBuilder()
                            .setUsername(body.getString(USERNAME_KEY))
                            .setProject(body.getString(PROJECT_KEY))
                            .setWellname(body.getString(WELLNAME_KEY))
                            .setLocation(body.getString(LOCATION_KEY))
                            .setBillingType(body.getString(BILLING_KEY))
                            .setStartTimeStampMillis(body.getLong(START_DATETIME_KEY))
                            .setEndTimestampMillis(body.getLong(END_DATETIME_KEY))
                            .setMileageStart(body.getInt(START_MILEAGE_KEY))
                            .setMileageEnd(body.getInt(END_MILEAGE_KEY))
                            .setDescription(body.getString(DESCRIPTION_KEY))
                            .setGPSCoords(body.getGpsCoord(GPS_KEY))
                            .build();

                    return mService.updateNote(body.getString(TOKEN_KEY),
                            body.getInt(TICKET_NUMBER_KEY), fieldNote);
                case LOGOUT:
                case REGISTER:
                default:
                    return mService.unsupportedNote(requestHeader);
            }
        } catch (Exception e) {
            return mService.malformedNote("MISSING FIELDS", e);
        }
    }
}
