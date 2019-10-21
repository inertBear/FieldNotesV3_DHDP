package com.devhunter.dhdp.services;

import com.devhunter.DHDPConnector4J.*;
import com.devhunter.DHDPConnector4J.groups.DHDPEntity;
import com.devhunter.DHDPConnector4J.groups.DHDPOrganization;
import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.devhunter.DHDPConnector4J.DHDPResponse.*;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;

public class CodecServiceTest {
    private CodecService mCodecService;

    @Before
    public void setup() {
        DHDPServiceRegistry registry = new DHDPServiceRegistry();
        CodecService.initService(registry);
        mCodecService = registry.resolve(CodecService.class);
    }

    /**
     * simulate receiving a request from a client, then decoding in to a DHDPRequest
     */
    @Test
    public void decodeTest() {
        String creator = "unit test";
        DHDPRequestType requestType = DHDPRequestType.UNKNOWN;
        DHDPOrganization organization = DHDPOrganization.UNKNOWN;
        DHDPEntity originator = DHDPEntity.UNKNOWN;
        DHDPEntity recipient = DHDPEntity.DHDP;

        String KEY_1 = "Key1";
        String KEY_2 = "Key2";
        String VALUE_1 = "Value1";
        String VALUE_2 = "Value2";

        // SETUP: simulate an encoded request fom a client
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put(KEY_1, VALUE_1);
        bodyMap.put(KEY_2, VALUE_2);

        DHDPRequest simRequest = DHDPRequest.newBuilder()
                .setHeader(DHDPHeader.newBuilder()
                        .setCreator(creator)
                        .setRequestType(requestType)
                        .setOrganization(organization)
                        .setOriginator(originator)
                        .setRecipient(recipient)
                        .build())
                .setBody(new DHDPBody(bodyMap))
                .build();

        JSONObject headerAndBody = new JSONObject();
        Iterator iterator = simRequest.getHeader().keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            headerAndBody.put(key, simRequest.getHeader().get(key));
        }
        iterator = simRequest.getBody().keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            headerAndBody.put(key, simRequest.getBody().get(key));
        }
        byte[] encodedBytes = Base64.encodeBase64(headerAndBody.toString().getBytes());
        String encodedString = new String(encodedBytes);


        // START TEST: decode request received from client
        DHDPRequest rxRequest = mCodecService.decode(encodedString);
        DHDPHeader requestHeader = rxRequest.getHeader();
        assertEquals(creator, requestHeader.getCreator());
        assertEquals(requestType, requestHeader.getRequestType());
        assertEquals(organization, requestHeader.getOrganization());
        assertEquals(originator, requestHeader.getOriginator());
        assertEquals(recipient, requestHeader.getRecipient());

        DHDPBody requestBody = rxRequest.getBody();
        assertEquals(VALUE_1, requestBody.get(KEY_1));
        assertEquals(VALUE_2, requestBody.get(KEY_2));
    }

    /**
     * encode test, simulates client receiving encoded result for verification
     */
    @Test
    public void encodeTest() {
        String creator = "unit test";
        DHDPRequestType requestType = DHDPRequestType.UNKNOWN;
        DHDPOrganization organization = DHDPOrganization.UNKNOWN;
        DHDPEntity originator = DHDPEntity.UNKNOWN;
        DHDPEntity recipient = DHDPEntity.DHDP;
        DHDPResponseType RESPONSE_TYPE = DHDPResponseType.SUCCESS;
        String MESSAGE = "response encoded";
        LocalDateTime now = LocalDateTime.now();

        DHDPHeader header = DHDPHeader.newBuilder()
                .setCreator(creator)
                .setRequestType(requestType)
                .setOrganization(organization)
                .setOriginator(originator)
                .setRecipient(recipient)
                .build();

        DHDPResponse response = DHDPResponse.newBuilder()
                .setHeader(header)
                .setStatus(RESPONSE_TYPE)
                .setMessage(MESSAGE)
                .setTimestamp(now)
                .setResults(null)
                .build();

        String encodedString = mCodecService.encode(response);


        // SIMULATE CLIENT RECEIVING encoded value
        byte[] decodedBytes = Base64.decodeBase64(encodedString.getBytes());
        String decodedString = new String(decodedBytes);

        JSONObject responseObject = new JSONObject(decodedString);
        JSONObject header1 = responseObject.getJSONObject("HEADER");
        DHDPResponse dhdpResponse = DHDPResponse.newBuilder()
                .setHeader(DHDPHeader.newBuilder()
                        .setCreator(header1.getString(DHDPHeader.CREATOR_KEY))
                        .setOrganization(DHDPOrganization.valueOf(header1.getString(DHDPHeader.ORGANIZATION_KEY)))
                        .setRequestType(DHDPRequestType.valueOf(header1.getString(DHDPHeader.REQUEST_TYPE_KEY)))
                        .setOriginator(DHDPEntity.valueOf(header1.getString(DHDPHeader.RECIPIENT_KEY)))
                        .setRecipient(DHDPEntity.valueOf(header1.getString(DHDPHeader.ORIGINATOR_KEY)))
                        .build())
                .setStatus(DHDPResponseType.valueOf(responseObject.getString(STATUS_KEY)))
                .setMessage(responseObject.getString(MESSAGE_KEY))
                .setTimestamp(LocalDateTime.parse(responseObject.getString(TIMESTAMP_KEY)))
                .setResults(null)
                .build();

        assertEquals(header.getCreator(), dhdpResponse.getHeader().getCreator());
        assertEquals(header.getOrganization(), dhdpResponse.getHeader().getOrganization());
        assertEquals(header.getRequestType(), dhdpResponse.getHeader().getRequestType());
        assertEquals(header.getOriginator(), dhdpResponse.getHeader().getRecipient());
        assertEquals(header.getRecipient(), dhdpResponse.getHeader().getOriginator());

        assertEquals(RESPONSE_TYPE, dhdpResponse.getResponseType());
        assertEquals(MESSAGE, dhdpResponse.getMessage());
        assertEquals(now, dhdpResponse.getTimestamp());
        assertNull(dhdpResponse.getResults());
    }
}
