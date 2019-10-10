package com.devhunter.dhdp.infrastructure;

import com.devhunter.DHDPConnector4J.DHDPRequest;
import com.devhunter.DHDPConnector4J.DHDPResponse;

public abstract class DHDPWorkflow {
    protected static final String STATUS_KEY = "STATUS";
    protected static final String MESSAGE_KEY = "MESSAGE";
    protected static final String TIMESTAMP_KEY = "TIMESTAMP";
    protected static final String RESULT_KEY = "RESULT";
    protected DHServiceRegistry mRegistry;

    protected DHDPWorkflow(DHServiceRegistry registry) {
        mRegistry = registry;
    }

    /**
     * process a request from a client
     *
     * @param request holding the data to process
     * @return response object created by the workflow
     */
    public abstract DHDPResponse process(DHDPRequest request);
}
