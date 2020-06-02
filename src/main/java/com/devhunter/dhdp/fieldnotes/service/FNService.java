package com.devhunter.dhdp.fieldnotes.service;

import com.devhunter.DHDPConnector4J.header.DHDPHeader;
import com.devhunter.DHDPConnector4J.response.DHDPResponseBody;
import com.devhunter.dhdp.fieldnotes.model.FieldNote;

import java.util.Map;

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
     * @return response message
     */
    DHDPResponseBody login(String username, String password);

    /**
     * adds a new note to the FieldNotes database
     *
     * @param token     token the user users authenticate the new ticket
     * @param fieldNote with the FieldNote to add
     * @return response message
     */
    DHDPResponseBody addNote(String token, FieldNote fieldNote);

    /**
     * updates and existing note in the FieldNotes database
     *
     * @param token        token the user users authenticate the new ticket
     * @param ticketNumber to update
     * @param fieldNote    containing the data to update to
     * @return response message
     */
    DHDPResponseBody updateNote(String token, int ticketNumber, FieldNote fieldNote);

    /**
     * deletes an existing note from the FieldNotes database
     *
     * @param token        the user users authenticate the new ticket
     * @param ticketNumber to delete
     * @return response message
     */
    DHDPResponseBody deleteNote(String token, int ticketNumber);

    /**
     * queries the FieldNotes database
     *
     * @param token            the user users authenticate the new ticket
     * @param searchParameters to filter search results
     * @return response message
     */
    DHDPResponseBody searchNote(String token, Map<String, Object> searchParameters);

    /**
     * handles unsupported operations sent from a client
     *
     * @param header with client information
     * @return response message
     */
    DHDPResponseBody unsupportedNote(DHDPHeader header);

    /**
     * handles malformed request from a client
     *
     * @param message to send to client
     * @param e throwable for the results
     * @return response message
     */
    DHDPResponseBody malformedNote(String message, Throwable e);
}
