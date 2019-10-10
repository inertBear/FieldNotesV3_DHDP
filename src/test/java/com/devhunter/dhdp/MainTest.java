package com.devhunter.dhdp;

import com.devhunter.DHDPConnector4J.*;
import com.devhunter.dhdp.testUtils.JsonParser;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MainTest {
    private static final String CREATOR_KEY = "CREATOR";
    private static final String ORGANIZATION_KEY = "ORGANIZATION";
    private static final String REQUEST_TYPE_KEY = "REQUEST_TYPE";
    private static final String ORIGINATOR_KEY = "ORIGINATOR";
    private static final String RECIPIENT_KEY = "RECIPIENT";
    private static final String USERNAME_KEY = "USERNAME";
    private static final String PASSWORD_KEY = "PASSWORD";

    private static final String CREATOR = "USER";
    private static final DHDPOrganization ORGANIZATION = DHDPOrganization.DEVHUNTER;
    private static final DHDPRequestType TYPE = DHDPRequestType.LOGIN;
    private static final DHDPEntity ORIGINATOR = DHDPEntity.FieldNotes;
    private static final DHDPEntity RECIPIENT = DHDPEntity.DHDP;
    private static final String USERNAME = "USER";
    private static final String PASSWORD = "Password123!@#";
    private DHDPRequestService mService;
    private static JsonParser mJsonParser = new JsonParser();

    @Before
    public void setup() {
        mService = DHDPRequestService.getInstance();
    }

    /**
     * Test connection to ServerSocket @ localhost
     */
    @Test
    public void testHttpGetWithJson() {

        DHDPHeader header = DHDPHeader.newBuilder()
                .setCreator(CREATOR)
                .setOrganization(ORGANIZATION)
                .setRequestType(TYPE)
                .setOriginator(ORIGINATOR)
                .setRecipient(RECIPIENT)
                .build();

        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put(USERNAME_KEY, USERNAME);
        bodyMap.put(PASSWORD_KEY, PASSWORD);
        DHDPTestBody body = new DHDPTestBody(bodyMap);

        DHDPResponse dhdpResponse = mService.sendRequest(DHDPRequest.newBuilder()
                .setHeader(header)
                .setBody(body)
                .build());
        assertEquals(DHDPResponseType.SUCCESS, dhdpResponse.getResponseType());
        System.out.print(dhdpResponse.getMessage());


//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("PING", "http://localhost:8080");

//        JSONObject postResponse = mJsonParser.createHttpRequest("http://localhost:8080?", jsonObject);
//        assertEquals("SUCCESS", postResponse.getString("STATUS"));
//        assertEquals("WE DID IT", postResponse.getString("MESSAGE"));
//        assertEquals("Results are overrated", postResponse.getString("RESULTS"));
    }

    /**
     * Test connection to ServerSocket @ localhost
     */
    @Test
    public void testHttpPostWithJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("PING", "Tester");
        jsonObject.put("AGE", "47");
        jsonObject.put("ADDRESS", "123 This rd");

        JSONObject postResponse = mJsonParser.createHttpRequest("http://localhost:8080?", jsonObject);
        assertEquals("success", postResponse.getString("status"));
        assertEquals("PING", postResponse.getString("message"));
    }

    private class DHDPTestBody extends DHDPBody {

        DHDPTestBody(Map<String, Object> map) {
            super(map);
        }
    }
}