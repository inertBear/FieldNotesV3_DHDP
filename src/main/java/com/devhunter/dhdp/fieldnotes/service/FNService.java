package com.devhunter.dhdp.fieldnotes.service;

import com.devhunter.DHDPConnector4J.DHDPBody;
import com.devhunter.dhdp.fieldnotes.model.FieldNoteResponse;

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
    FieldNoteResponse login(DHDPBody body);

    /**
     * adds a new note to the FieldNotes database
     *
     * @param body with the note to add's data
     * @return fieldNote with result status
     */
    FieldNoteResponse addNote(DHDPBody body);

    /**
     * updates and existing note in the FieldNotes database
     *
     * @param body with the information to update
     * @return fieldNote with result status
     */
    FieldNoteResponse updateNote(DHDPBody body);

    /**
     * deletes an existing note from the FieldNotes database
     *
     * @param body with the note to delete
     * @return fieldNote with result status
     */
    FieldNoteResponse deleteNote(DHDPBody body);

    /**
     * queries the FieldNotes database
     *
     * @param body contains search parameters
     * @return fieldNote with search results
     */
    FieldNoteResponse searchNote(DHDPBody body);

    /**
     * handles unsupported operations called by a client
     *
     * @param body with client information
     * @return fieldNote with failure message
     */
    FieldNoteResponse unsupportedNote(DHDPBody body);
}
