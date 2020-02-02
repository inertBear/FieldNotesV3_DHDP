package com.devhunter.dhdp.services;

import com.devhunter.DHDPConnector4J.request.DHDPRequest;
import com.devhunter.DHDPConnector4J.response.DHDPResponse;
import com.devhunter.dhdp.infrastructure.DHDPService;
import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.codec.binary.Base64;

import java.util.logging.Logger;

import static com.devhunter.dhdp.infrastructure.DHDPConstants.CODEC_SERVICE_NAME;

/**
 * Encodes and Decodes client communications.
 * This Service is registered with the Service Registry upon startup
 */
public class CodecService extends DHDPService {
    private static Logger mLogger = Logger.getLogger(CodecService.class.getSimpleName());

    private CodecService(String name) {
        super(name);
    }

    public static void initService(DHDPServiceRegistry registry) {
        if (!registry.containsService(CodecService.class)) {
            registry.register(CodecService.class, new CodecService(CODEC_SERVICE_NAME));
        }
    }

    /**
     * convert to JSON and encode a DHDPResponse
     *
     * @param response DHDPResponse to encode
     * @return encoded value
     */
    public String encode(DHDPResponse response) {
        // convert
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String responseString = gson.toJson(response, DHDPResponse.class);
        // encode
        byte[] encodedBytes = Base64.encodeBase64(responseString.getBytes());

        mLogger.info("DHDP - transmitting response: " + responseString);
        return new String(encodedBytes);
    }

    /**
     * decode JSON and convert into a DHDPRequest
     *
     * @param value String to decode
     * @return decoded value
     */
    public DHDPRequest decode(String value) {
        // decode
        byte[] decodedBytes = Base64.decodeBase64(value);
        // convert
        String decodedRequest = new String(decodedBytes);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        DHDPRequest request = gson.fromJson(decodedRequest, DHDPRequest.class);

        mLogger.info("DHDP - received request: " + gson.toJson(request));
        return request;
    }
}
