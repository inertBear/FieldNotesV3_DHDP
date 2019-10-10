package com.devhunter.dhdp.fieldnotes;

import com.devhunter.DHDPConnector4J.DHDPBody;

import java.util.Map;

public class FieldNote extends DHDPBody {

    public FieldNote(Map<String, Object> bodyMap) throws IllegalArgumentException {
        super(bodyMap);
    }
}
