package com.devhunter.dhdp.fieldnotes.service;

import com.devhunter.dhdp.fieldnotes.FieldNotesConstants;
import com.devhunter.dhdp.fieldnotes.model.FieldNote;
import com.devhunter.dhdp.infrastructure.DHDPService;
import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;

import java.util.Arrays;
import java.util.List;

import static com.devhunter.dhdp.fieldnotes.FieldNotesConstants.FIELDNOTES_VALIDATION_SERVICE_NAME;

/**
 * validates the FieldNotes received from a client
 * <p>
 * Clients can/should preform their own verification of fields
 */
public class FieldNoteValidationService extends DHDPService {

    private FieldNoteValidationService(String name) {
        super(name);
    }

    public static void initService(DHDPServiceRegistry registry) {
        if (!registry.containsService(FieldNoteValidationService.class)) {
            registry.register(FieldNoteValidationService.class,
                    new FieldNoteValidationService(FIELDNOTES_VALIDATION_SERVICE_NAME));
        }
    }

    /**
     * validate login information
     * 1) username - required
     * 2) password - required
     *
     * @param username to validate
     * @param password to validate
     * @throws IllegalArgumentException if a value is not present
     */
    void validateLogin(final String username, final String password) throws IllegalArgumentException {
        String messageTemplate = "Cannot Login without a ";

        // ensure username was sent from client
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException(messageTemplate + "Username");
        }

        // ensure password was sent from client
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException(messageTemplate + "Password");
        }
    }

    /**
     * Validates a request to add a new FieldNote from a client
     * REQUIRED
     * 1) username
     * 2) project
     * 3) wellname
     * 4) location
     * 5) billing type
     * 6) timestamps
     * 7) description
     * <p>
     * OPTIONAL
     * 8) mileage
     * 9) gps
     *
     * @param fieldNote containing the values to insert
     * @throws IllegalArgumentException if validation fails
     */
    void validateAddNote(final FieldNote fieldNote) throws IllegalArgumentException {
        String messageTemplate = "Cannot add a new FieldNote without a ";

        // username
        String username = fieldNote.getUsername();
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException(messageTemplate + "Username");
        }

        // project
        String project = fieldNote.getProject();
        if (project == null || project.isEmpty()) {
            throw new IllegalArgumentException(messageTemplate + "Project");
        }

        // wellname
        String wellname = fieldNote.getWellname();
        if (wellname == null || wellname.isEmpty()) {
            throw new IllegalArgumentException(messageTemplate + "Well Name");
        }

        // location
        String location = fieldNote.getLocation();
        if (location == null || location.isEmpty()) {
            throw new IllegalArgumentException(messageTemplate + "Location");
        } else {
            //validate values
            List<String> locations = Arrays.asList(FieldNotesConstants.LOCATION_VALUES);
            if (!locations.contains(location)) {
                throw new IllegalArgumentException(messageTemplate + "valid Location");
            }
        }

        // billing type
        String billing = fieldNote.getBillingType();
        if (billing == null || billing.isEmpty()) {
            throw new IllegalArgumentException(messageTemplate + "Billing Type");
        } else {
            //validate values
            List<String> billingTypes = Arrays.asList(FieldNotesConstants.BILLING_VALUES);
            if (!billingTypes.contains(billing)) {
                throw new IllegalArgumentException(messageTemplate + "valid Billing Type");
            }
        }

        // start timestamp
        if (!fieldNote.hasStartTimeStamp()) {
            throw new IllegalArgumentException(messageTemplate + "Start Timestamp");
        }

        //end timestamp
        if (!fieldNote.hasEndTimeStamp()) {
            throw new IllegalArgumentException(messageTemplate + "End Timestamp");
        }

        // description
        String description = fieldNote.getDescription();
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException(messageTemplate + "Description");
        }
    }
}
