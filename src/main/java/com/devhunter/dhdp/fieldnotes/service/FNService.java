package com.devhunter.dhdp.fieldnotes.service;

import com.devhunter.DHDPConnector4J.header.DHDPHeader;
import com.devhunter.DHDPConnector4J.request.DHDPRequestBody;
import com.devhunter.DHDPConnector4J.response.DHDPResponseBody;

/**
 * interface for the FieldNotes service.
 * The methods described here align with the customer requirements
 */
public interface FNService {

    /**
     * logs into FieldNotes database
     *
     * @param body with login information
     * @return fieldNote with result status
     */
    DHDPResponseBody login(DHDPRequestBody body);

    /**
     * adds a new note to the FieldNotes database
     *
     * @param body with the note to add's data
     * @return fieldNote with result status
     */
    DHDPResponseBody addNote(DHDPRequestBody body);

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
     * @param body with the note to delete
     * @return fieldNote with result status
     */
    DHDPResponseBody deleteNote(DHDPRequestBody body);

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
