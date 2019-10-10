package com.devhunter.dhdp;

import com.devhunter.DHDPConnector4J.DHDPEntity;
import com.devhunter.DHDPConnector4J.DHDPHeader;
import com.devhunter.dhdp.fieldnotes.FieldNotesWorkflow;
import com.devhunter.dhdp.infrastructure.DHDPWorkflow;
import com.devhunter.dhdp.infrastructure.DHServiceRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * This class uses the information received in the header of a
 * Request to determine which workflow to use.
 * <p>
 * DHDPWorkflows are mapped to the DHDPEntity that originated the request.
 */
public class DHDPWorkflowHandler {
    private Map<DHDPEntity, DHDPWorkflow> mWorkflowMap = new HashMap<>();

    public DHDPWorkflowHandler(DHServiceRegistry registry) {
        initWorkflows(registry);
    }

    /**
     * retrieve workflow from mapping by originator
     *
     * @param header to determine originator
     * @return the workflow that will process the request
     */
    public DHDPWorkflow getWorkflow(DHDPHeader header) {
        // get originator from requestHeader
        DHDPEntity originator = header.getEnum(DHDPEntity.class, DHDPHeader.ORIGINATOR_KEY);
        // return the corresponding workflow
        return mWorkflowMap.get(originator);
    }

    /**
     * initializes all workflows with the service registry
     *
     * @param registry containing services
     */
    private void initWorkflows(DHServiceRegistry registry) {
        mWorkflowMap.put(DHDPEntity.FieldNotes, new FieldNotesWorkflow(registry));
    }
}
