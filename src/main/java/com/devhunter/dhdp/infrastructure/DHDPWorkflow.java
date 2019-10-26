package com.devhunter.dhdp.infrastructure;

import com.devhunter.DHDPConnector4J.request.DHDPRequest;
import com.devhunter.DHDPConnector4J.response.DHDPResponseBody;

/**
 * abstract class for a workflow.
 * <p>
 * All workflows will need to provide
 * their won implementation of process(), since each client will need
 * their data processed differently
 */
public abstract class DHDPWorkflow {
    private String mName;
    protected DHDPServiceRegistry mRegistry;

    protected DHDPWorkflow(String name, DHDPServiceRegistry registry) {
        mName = name;
        mRegistry = registry;
    }

    /**
     * process a request from a client
     *
     * @param request holding the data to process
     * @return response object created by the workflow
     */
    public abstract DHDPResponseBody process(DHDPRequest request);

    /**
     * retrieve the name of the workflow
     *
     * @return name of workflow
     */
    public String getName() {
        return mName;
    }
}
