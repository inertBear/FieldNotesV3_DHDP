package com.devhunter.dhdp;

import com.devhunter.DHDPConnector4J.*;
import com.devhunter.DHDPConnector4J.groups.DHDPEntity;
import com.devhunter.DHDPConnector4J.groups.DHDPOrganization;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.devhunter.DHDPConnector4J.constants.FieldNotesConstants.TOKEN_KEY;
import static com.devhunter.dhdp.fieldnotes.FieldNotesConstants.PASSWORD_TAG;
import static com.devhunter.dhdp.fieldnotes.FieldNotesConstants.USERNAME_TAG;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class DHDPTest {

    /**
     * END-TO-END FieldNotes Login test
     */
    @Test
    public void testDHDP() {
        final String creator = "Unit Test";
        final String username = "keithh";
        final String password = "hunterk";
        final String token = "1159616266";
        final String responseMessage = "Login Successful";

        // build request
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put(USERNAME_TAG, username);
        bodyMap.put(PASSWORD_TAG, password);
        DHDPBody body = new DHDPBody(bodyMap);

        DHDPRequest request = DHDPRequest.newBuilder()
                .setHeader(DHDPHeader.newBuilder()
                        .setCreator(creator)
                        .setOrganization(DHDPOrganization.DEVHUNTER)
                        .setRequestType(DHDPRequestType.LOGIN)
                        .setOriginator(DHDPEntity.FieldNotes)
                        .setRecipient(DHDPEntity.DHDP)
                        .build())
                .setBody(body)
                .build();

        // send request to DHDP
        DHDPResponse response = DHDPRequestService.getInstance().sendRequest(request);

        // check response header
        DHDPHeader header = response.getHeader();
        assertEquals(creator, header.getCreator());
        assertEquals(DHDPOrganization.DEVHUNTER, header.getOrganization());
        assertEquals(DHDPRequestType.LOGIN, header.getRequestType());
        assertEquals(DHDPEntity.DHDP, header.getOriginator());
        assertEquals(DHDPEntity.FieldNotes, header.getRecipient());

        // check response body
        assertEquals(DHDPResponseType.SUCCESS, response.getResponseType());
        assertEquals(responseMessage, response.getMessage());
        assertEquals(token, response.getResults().get(0).get(TOKEN_KEY));
        assertNotNull(response.getTimestamp());
    }
}