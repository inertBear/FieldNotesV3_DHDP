package com.devhunter.dhdp;

import com.devhunter.DHDPConnector4J.groups.DHDPEntity;
import com.devhunter.DHDPConnector4J.header.DHDPHeader;
import com.devhunter.dhdp.fieldnotes.workflow.FieldNotesWorkflow;
import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;
import com.devhunter.dhdp.infrastructure.DHDPWorkflow;

import java.util.HashMap;
import java.util.Map;

/**
 * This class uses the information received in the header of a
 * Request to determine which workflow to use.
 * <p>
 * DHDPWorkflows are mapped to the DHDPEntity that originated the request.
 */
class DHDPWorkflowHandler {
    private Map<DHDPEntity, DHDPWorkflow> mWorkflowMap = new HashMap<>();

    DHDPWorkflowHandler(DHDPServiceRegistry registry) {
        initWorkflows(registry);
    }

    /**
     * retrieve workflow from mapping by the originator of a request
     *
     * @param header to determine originator
     * @return the workflow that will process the request
     */
    DHDPWorkflow getWorkflow(DHDPHeader header) {
        // get originator from requestHeader
        DHDPEntity originator = header.getOriginator();
        // return the corresponding workflow
        return mWorkflowMap.get(originator);
    }

    /**
     * initializes all workflows. Workflows are created to be used by
     * users of a specific DHDPEntity AKA, the sender of the request.
     *
     * @param registry containing services
     */
    private void initWorkflows(DHDPServiceRegistry registry) {
        mWorkflowMap.put(DHDPEntity.FieldNotes, new FieldNotesWorkflow("FieldNotesWorkflow", registry));
    }
}
