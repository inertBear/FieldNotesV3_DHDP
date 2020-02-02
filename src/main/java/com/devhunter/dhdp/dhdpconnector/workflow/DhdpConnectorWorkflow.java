package com.devhunter.dhdp.dhdpconnector.workflow;

import com.devhunter.DHDPConnector4J.header.DHDPHeader;
import com.devhunter.DHDPConnector4J.request.DHDPRequest;
import com.devhunter.DHDPConnector4J.response.DHDPResponseBody;
import com.devhunter.DHDPConnector4J.response.DHDPResponseType;
import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;
import com.devhunter.dhdp.infrastructure.DHDPWorkflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * defines the workflow for sending test requests from dhdpconnector.
 * <p>
 * Used for automated testing only.
 */
public class DhdpConnectorWorkflow extends DHDPWorkflow {

    public DhdpConnectorWorkflow(String name, DHDPServiceRegistry registry) {
        super(name, registry);
    }

    @Override
    public DHDPResponseBody process(DHDPRequest request) {
        DHDPHeader requestHeader = request.getHeader();

        // send an automatic response for request
        switch (requestHeader.getRequestType()) {
            case LOGIN:
                ArrayList<Map<String, String>> results = new ArrayList<>();
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("TOKEN", "123456789");
                results.add(resultMap);

                return DHDPResponseBody.newBuilder()
                        .setResponseType(DHDPResponseType.SUCCESS)
                        .setMessage("Login Successful")
                        .setResults(results)
                        .build();
            case ADD:
            case DELETE:
            case SEARCH:
            case UPDATE:
            case LOGOUT:
            case REGISTER:
            default:
                return DHDPResponseBody.newBuilder()
                        .setResponseType(DHDPResponseType.FAILURE)
                        .setMessage("Unsupported RequestType: " + requestHeader.getRequestType())
                        .setResults(null)
                        .build();
        }
    }
}
