package com.devhunter.dhdp.services;

import com.devhunter.DHDPConnector4J.header.DHDPHeader;
import com.devhunter.DHDPConnector4J.request.DHDPRequest;
import com.devhunter.DHDPConnector4J.request.DHDPRequestBody;
import com.devhunter.DHDPConnector4J.request.DHDPRequestType;
import com.devhunter.DHDPConnector4J.response.DHDPResponse;
import com.devhunter.DHDPConnector4J.response.DHDPResponseBody;
import com.devhunter.DHDPConnector4J.response.DHDPResponseType;
import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;
import com.devhunter.dhdp.testUtils.TestBody;
import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;

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
        String organization = "UNKNOWN";
        String originator = "UNKNOWN";
        String recipient = "DHDP";

        String KEY_1 = "Key1";
        String KEY_2 = "Key2";
        String VALUE_1 = "Value1";
        String VALUE_2 = "Value2";

        // SETUP: simulate an encoded request fom a client
        TestBody body = new TestBody();
        body.put(KEY_1, VALUE_1);
        body.put(KEY_2, VALUE_2);

        DHDPRequest simRequest = DHDPRequest.newBuilder()
                .setHeader(DHDPHeader.newBuilder()
                        .setCreator(creator)
                        .setRequestType(requestType)
                        .setOrganization(organization)
                        .setOriginator(originator)
                        .setRecipient(recipient)
                        .build())
                .setBody(body)
                .build();

        // encode a request
        Gson gson = new Gson();
        String requestString = gson.toJson(simRequest);
        byte[] encodedBytes = Base64.encodeBase64(requestString.getBytes());
        String encodedString = new String(encodedBytes);

        // START TEST: decode request received from client
        DHDPRequest rxRequest = mCodecService.decode(encodedString);
        DHDPHeader requestHeader = rxRequest.getHeader();
        assertEquals(creator, requestHeader.getCreator());
        assertEquals(requestType, requestHeader.getRequestType());
        assertEquals(organization, requestHeader.getOrganization());
        assertEquals(originator, requestHeader.getOriginator());
        assertEquals(recipient, requestHeader.getRecipient());

        DHDPRequestBody requestBody = rxRequest.getBody();
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
        String organization = "UNKNOWN";
        String originator = "UNKNOWN";
        String recipient = "DHDP";
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
                .setResponse(DHDPResponseBody.newBuilder()
                        .setResponseType(RESPONSE_TYPE)
                        .setMessage(MESSAGE)
                        .setResults(new ArrayList<>())
                        .build())
                .build();

        String encodedString = mCodecService.encode(response);

        // SIMULATE CLIENT RECEIVING encoded value
        byte[] decodedBytes = Base64.decodeBase64(encodedString.getBytes());
        String decodedString = new String(decodedBytes);

        // encode a request
        Gson gson = new Gson();
        DHDPResponse decodedResponse = gson.fromJson(decodedString, DHDPResponse.class);

        assertEquals(header.getCreator(), decodedResponse.getHeader().getCreator());
        assertEquals(header.getOrganization(), decodedResponse.getHeader().getOrganization());
        assertEquals(header.getRequestType(), decodedResponse.getHeader().getRequestType());
        assertEquals(recipient, decodedResponse.getHeader().getRecipient());
        assertEquals(originator, decodedResponse.getHeader().getOriginator());

        assertEquals(RESPONSE_TYPE, decodedResponse.getResponse().getResponseType());
        assertEquals(MESSAGE, decodedResponse.getResponse().getMessage());
    }
}
