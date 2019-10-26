package com.devhunter.dhdp;

import com.devhunter.DHDPConnector4J.DHDPRequestService;
import com.devhunter.DHDPConnector4J.groups.DHDPEntity;
import com.devhunter.DHDPConnector4J.groups.DHDPOrganization;
import com.devhunter.DHDPConnector4J.header.DHDPHeader;
import com.devhunter.DHDPConnector4J.request.DHDPRequest;
import com.devhunter.DHDPConnector4J.request.DHDPRequestType;
import com.devhunter.DHDPConnector4J.response.DHDPResponse;
import com.devhunter.DHDPConnector4J.response.DHDPResponseType;
import com.devhunter.dhdp.testUtils.TestBody;
import org.junit.Test;

import static com.devhunter.DHDPConnector4J.constants.fieldNotes.FieldNotesConstants.*;
import static junit.framework.TestCase.assertEquals;

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
        TestBody body = new TestBody();
        body.put(USERNAME_KEY, username);
        body.put(PASSWORD_KEY, password);

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
        assertEquals(DHDPResponseType.SUCCESS, response.getResponse().getResponseType());
        assertEquals(responseMessage, response.getResponse().getMessage());
        assertEquals(token, response.getResponse().getResults().get(0).get(TOKEN_KEY));
    }
}