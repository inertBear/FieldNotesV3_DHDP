package com.devhunter.dhdp.fieldnotes.model;

import com.devhunter.DHDPConnector4J.DHDPResponseType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

/**
 * Models an internal response to a FieldNotes request. Acts as a medium for
 * transfer between the db query resultSet and the DHDPResponse
 */
public class FieldNoteResponse {
    private DHDPResponseType mStatus;
    private String mMessage;
    private LocalDateTime mTimestamp;
    private ArrayList<Map<String, String>> mResults;

    public void setStatus(DHDPResponseType status) {
        mStatus = status;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        mTimestamp = timestamp;
    }

    public void addResults(Map<String, String> results) {
        if (mResults == null) {
            mResults = new ArrayList<>();
        }
        mResults.add(results);
    }

    public DHDPResponseType getStatus() {
        return mStatus;
    }

    public String getMessage() {
        return mMessage;
    }

    public LocalDateTime getTimestamp() {
        return mTimestamp;
    }

    public ArrayList<Map<String, String>> getResults() {
        return mResults;
    }
}
