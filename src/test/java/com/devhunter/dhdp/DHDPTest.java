package com.devhunter.dhdp;

import com.devhunter.DHDPConnector4J.DHDPRequestService;
import com.devhunter.DHDPConnector4J.header.DHDPHeader;
import com.devhunter.DHDPConnector4J.request.DHDPRequest;
import com.devhunter.DHDPConnector4J.request.DHDPRequestType;
import com.devhunter.DHDPConnector4J.response.DHDPResponse;
import com.devhunter.DHDPConnector4J.response.DHDPResponseType;
import com.devhunter.dhdp.testUtils.TestBody;
import org.junit.Test;

import static com.devhunter.dhdp.dhdpconnector.DhdpConnectorConstants.UNIT_TEST_CREATOR;
import static com.devhunter.dhdp.dhdpconnector.DhdpConnectorConstants.UNIT_TEST_ORGANIZATION;
import static com.devhunter.dhdp.fieldnotes.FieldNotesConstants.*;
import static com.devhunter.dhdp.infrastructure.DHDPConstants.DHDP_NAME;
import static junit.framework.TestCase.assertEquals;

public class DHDPTest {

    /**
     * END-TO-END FieldNotes Login test
     */
    @Test
    public void testDHDP() {
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
                        .setCreator(UNIT_TEST_CREATOR)
                        .setOrganization(UNIT_TEST_ORGANIZATION)
                        .setRequestType(DHDPRequestType.LOGIN)
                        .setOriginator(FIELDNOTES_SERVICE_NAME)
                        .setRecipient(DHDP_NAME)
                        .build())
                .setBody(body)
                .build();

        // send request to DHDP
        DHDPResponse response = DHDPRequestService.getInstance().sendRequest(request);

        // check response header
        DHDPHeader header = response.getHeader();
        assertEquals(UNIT_TEST_CREATOR, header.getCreator());
        assertEquals(UNIT_TEST_ORGANIZATION, header.getOrganization());
        assertEquals(DHDPRequestType.LOGIN, header.getRequestType());
        assertEquals(DHDP_NAME, header.getOriginator());
        assertEquals(FIELDNOTES_SERVICE_NAME, header.getRecipient());

        // check response body
        assertEquals(DHDPResponseType.SUCCESS, response.getResponse().getResponseType());
        assertEquals(responseMessage, response.getResponse().getMessage());
        assertEquals(token, response.getResponse().getResults().get(0).get(TOKEN_KEY));
    }
}