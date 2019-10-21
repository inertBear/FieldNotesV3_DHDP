package com.devhunter.dhdp.infrastructure;

/**
 * abstract class for a service. This abstraction provides
 * the mechanism for client services to be added to the
 * DHDPServiceRegistry.
 */
public abstract class DHDPService {
    private String mName;

    public DHDPService(String name) {
        mName = name;
    }

    /**
     * retrieve the name of a service
     *
     * @return name of service
     */
    String getName() {
        return mName;
    }
}
