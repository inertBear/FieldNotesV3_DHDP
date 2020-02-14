package com.devhunter.dhdp;

import com.devhunter.DHDPConnector4J.header.DHDPHeader;
import com.devhunter.dhdp.dhdpconnector.workflow.DhdpConnectorWorkflow;
import com.devhunter.dhdp.fieldnotes.workflow.FieldNotesWorkflow;
import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;
import com.devhunter.dhdp.infrastructure.DHDPWorkflow;

import java.util.HashMap;
import java.util.Map;

import static com.devhunter.dhdp.dhdpconnector.DhdpConnectorConstants.DHDPCONNECTOR_SERVICE_NAME;
import static com.devhunter.dhdp.dhdpconnector.DhdpConnectorConstants.DHDPCONNECTOR_WORKFLOW_NAME;
import static com.devhunter.dhdp.fieldnotes.FieldNotesConstants.FIELDNOTES_SERVICE_NAME;
import static com.devhunter.dhdp.fieldnotes.FieldNotesConstants.FIELDNOTES_WORKFLOW_NAME;

/**
 * This class uses the information received in the header of a
 * Request to determine which workflow to use.
 * <p>
 * DHDPWorkflows are mapped to the Originator of a request.
 */
class DHDPWorkflowHandler {
    private Map<String, DHDPWorkflow> mWorkflowMap = new HashMap<>();

    DHDPWorkflowHandler(final DHDPServiceRegistry registry) {
        initWorkflows(registry);
    }

    /**
     * retrieve workflow from mapping by the originator of a request
     *
     * @param header to determine originator
     * @return the workflow that will process the request
     */
    DHDPWorkflow getWorkflow(final DHDPHeader header) {
        // get originator from requestHeader
        String originator = header.getOriginator();
        // return the corresponding workflow
        return mWorkflowMap.get(originator);
    }

    /**
     * initializes all workflows. Workflows are created to be used by
     * users of a specific DHDPEntity AKA, the sender of the request.
     *
     * @param registry containing services
     */
    private void initWorkflows(final DHDPServiceRegistry registry) {
        mWorkflowMap.put(DHDPCONNECTOR_SERVICE_NAME, new DhdpConnectorWorkflow(DHDPCONNECTOR_WORKFLOW_NAME, registry));
        mWorkflowMap.put(FIELDNOTES_SERVICE_NAME, new FieldNotesWorkflow(FIELDNOTES_WORKFLOW_NAME, registry));
    }
}
