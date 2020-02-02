package com.devhunter.dhdp.fieldnotes.service;

import com.devhunter.dhdp.fieldnotes.FieldNotesConstants;
import com.devhunter.dhdp.fieldnotes.model.FieldNote;
import com.devhunter.dhdp.infrastructure.DHDPService;
import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.devhunter.dhdp.fieldnotes.FieldNotesConstants.FIELDNOTES_VALIDATION_SERVICE_NAME;

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
     *
     * @param username to validate
     * @param password to validate
     * @throws IllegalArgumentException if a value is not present
     */
    void validateLogin(String username, String password) throws IllegalArgumentException {
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
     *
     * @param fieldNote containing the values to insert
     * @throws IllegalArgumentException if validation fails
     */
    void validateAddNote(FieldNote fieldNote) throws IllegalArgumentException {
        String messageTemplate = "Cannot add a new FieldNote without a ";

        // ensure username was sent from client
        String username = fieldNote.getUsername();
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException(messageTemplate + "Username");
        }

        // ensure project was sent from client
        String project = fieldNote.getProject();
        if (project == null || project.isEmpty()) {
            throw new IllegalArgumentException(messageTemplate + "Project");
        }

        // ensure well name was sent from client
        String wellname = fieldNote.getWellname();
        if (wellname == null || wellname.isEmpty()) {
            throw new IllegalArgumentException(messageTemplate + "Well Name");
        }

        // ensure location was sent from client
        String location = fieldNote.getLocation();
        if (location == null || location.isEmpty()) {
            throw new IllegalArgumentException(messageTemplate + "Location");
        } else {
            //validate location values
            List<String> locations = Arrays.asList(FieldNotesConstants.LOCATION_VALUES);
            if (!locations.contains(location)) {
                throw new IllegalArgumentException(messageTemplate + "valid Location");
            }
        }

        // ensure billing type was sent from client
        String billing = fieldNote.getBilling();
        if (billing == null || billing.isEmpty()) {
            throw new IllegalArgumentException(messageTemplate + "Billing Type");
        } else {
            //validate billing values
            List<String> billingTypes = Arrays.asList(FieldNotesConstants.BILLING_VALUES);
            if (!billingTypes.contains(billing)) {
                throw new IllegalArgumentException(messageTemplate + "valid Billing Type");
            }
        }

        // ensure start timestamp was sent from client
        LocalDateTime startTimestamp = fieldNote.getStartTimestamp();
        if (startTimestamp == null) {
            throw new IllegalArgumentException(messageTemplate + "Start Timestamp");
        }

        // ensure end timestamp was sent from client
        LocalDateTime endTimestamp = fieldNote.getEndTimestamp();
        if (endTimestamp == null) {
            throw new IllegalArgumentException(messageTemplate + "End Timestamp");
        }

        // ensure end description was sent from client
        String description = fieldNote.getDescription();
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException(messageTemplate + "Description");
        }
    }
}
