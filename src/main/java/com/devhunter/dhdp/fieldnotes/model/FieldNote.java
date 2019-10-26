package com.devhunter.dhdp.fieldnotes.model;

import com.devhunter.DHDPConnector4J.request.DHDPRequestBody;

/**
 * Models an internal response to a FieldNotes request. Acts as a medium for
 * transfer between the db query resultSet and the DHDPResponse
 */
public class FieldNote extends DHDPRequestBody {
    private String mUsername;
    private String mPassword;

    public void setUsername(String username) {
        mUsername = username;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getPassword() {
        return mPassword;
    }

}
