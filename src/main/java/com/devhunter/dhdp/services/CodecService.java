package com.devhunter.dhdp.services;

import com.devhunter.DHDPConnector4J.*;
import com.devhunter.DHDPConnector4J.groups.DHDPEntity;
import com.devhunter.DHDPConnector4J.groups.DHDPOrganization;
import com.devhunter.dhdp.infrastructure.DHDPService;
import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Encodes and Decodes client communications.
 * This Service is registered with the Service Registry upon startup
 */
public class CodecService extends DHDPService {

    private CodecService(String name) {
        super(name);
    }

    public static void initService(DHDPServiceRegistry registry) {
        if (!registry.containsService(CodecService.class)) {
            registry.register(CodecService.class, new CodecService("CodecService"));
        }
    }

    /**
     * Encode a DHDPResponse to a String
     *
     * @param response DHDPResponse to encode
     * @return encoded value
     */
    public String encode(DHDPResponse response) {
        // convert to String
        String value = response.toString();
        // encode
        byte[] encodedBytes = Base64.encodeBase64(value.getBytes());
        return new String(encodedBytes);
    }

    /**
     * Decode a String into a DHDPRequest
     *
     * @param value String to decode
     * @return decoded value
     */
    public DHDPRequest decode(String value) {
        // decode
        byte[] decodedBytes = Base64.decodeBase64(value);
        // convert to JSONObject
        JSONObject requestObject = new JSONObject(new String(decodedBytes));

        //convert to DHDPRequest
        DHDPHeader header = DHDPHeader.newBuilder()
                .setCreator(requestObject.getString(DHDPHeader.CREATOR_KEY))
                .setRequestType(requestObject.getEnum(DHDPRequestType.class, DHDPHeader.REQUEST_TYPE_KEY))
                .setOrganization(requestObject.getEnum(DHDPOrganization.class, DHDPHeader.ORGANIZATION_KEY))
                .setOriginator(requestObject.getEnum(DHDPEntity.class, DHDPHeader.ORIGINATOR_KEY))
                .setRecipient(requestObject.getEnum(DHDPEntity.class, DHDPHeader.RECIPIENT_KEY))
                .build();

        // remove header values from request
        requestObject.remove(DHDPHeader.CREATOR_KEY);
        requestObject.remove(DHDPHeader.REQUEST_TYPE_KEY);
        requestObject.remove(DHDPHeader.ORGANIZATION_KEY);
        requestObject.remove(DHDPHeader.ORIGINATOR_KEY);
        requestObject.remove(DHDPHeader.RECIPIENT_KEY);

        // put the rest of the values in the body
        Map<String, Object> bodyMap = new HashMap<>();
        for (String each : requestObject.keySet()) {
            bodyMap.put(each, requestObject.get(each));
        }

        return DHDPRequest.newBuilder()
                .setHeader(header)
                .setBody(new DHDPBody(bodyMap))
                .build();
    }
}
