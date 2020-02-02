package com.devhunter.dhdp.fieldnotes.service;

import com.devhunter.DHDPConnector4J.header.DHDPHeader;
import com.devhunter.DHDPConnector4J.request.DHDPRequestBody;
import com.devhunter.DHDPConnector4J.response.DHDPResponseBody;
import com.devhunter.dhdp.fieldnotes.model.FieldNote;

/**
 * interface for the FieldNotes service.
 * The methods described here align with the customer requirements
 */
public interface FNService {

    /**
     * logs into FieldNotes database
     *
     * @param username to log in with
     * @param password to log in with
     * @return fieldNote with result status
     */
    DHDPResponseBody login(String username, String password);

    /**
     * adds a new note to the FieldNotes database
     *
     * @param token     token the user users authenticate the new ticket
     * @param fieldNote with the FieldNote to add
     * @return fieldNote with result status
     */
    DHDPResponseBody addNote(String token, FieldNote fieldNote);

    /**
     * updates and existing note in the FieldNotes database
     *
     * @param body with the information to update
     * @return fieldNote with result status
     */
    DHDPResponseBody updateNote(DHDPRequestBody body);

    /**
     * deletes an existing note from the FieldNotes database
     *
     * @param token        the user users authenticate the new ticket
     * @param ticketNumber to delete
     * @return result status
     */
    DHDPResponseBody deleteNote(String token, int ticketNumber);

    /**
     * queries the FieldNotes database
     *
     * @param body contains search parameters
     * @return fieldNote with search results
     */
    DHDPResponseBody searchNote(DHDPRequestBody body);

    /**
     * handles unsupported operations called by a client
     *
     * @param header with client information
     * @return fieldNote with failure message
     */
    DHDPResponseBody unsupportedNote(DHDPHeader header);
}
