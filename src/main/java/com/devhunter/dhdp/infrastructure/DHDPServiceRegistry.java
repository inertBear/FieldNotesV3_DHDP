package com.devhunter.dhdp.infrastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DHDPServiceRegistry holds instances of service so that they can be retrieved and used by
 * anything that has access to the DHDPServiceRegistry
 * <p>
 * 1) a specific service class can only be registered once
 * 2) services are resolved by class
 * <p>
 * based on "Joshua Bloch's typesafe hetereogeneous container pattern"
 */
public class DHDPServiceRegistry {
    private Logger mLogger = Logger.getLogger(DHDPServiceRegistry.class.getName());
    private Map<Class<?>, Object> mServiceMap = new HashMap<>();

    public DHDPServiceRegistry() {
    }

    /**
     * adds a new service to the DHDPServiceRegistry
     * If the service class is already registered, does nothing
     * NOTE: will NOT overwrite previously added services
     *
     * @param klass   the Class of the service to register
     * @param service the concrete service being registered
     */
    public <T extends DHDPService> void register(Class<T> klass, T service) {
        // check if class is already registered
        if (!mServiceMap.containsKey(klass)) {
            mServiceMap.put(klass, service);
        }
    }

    /**
     * retrieves a service from the DHDPServiceRegistry by class
     * If the name is not contained in the registry, returns null
     *
     * @param klass to retrieve from the registry
     * @return T the registered service, cast as {@param klass}
     */
    public <T extends DHDPService> T resolve(Class<T> klass) {
        // check if name is registered
        if (mServiceMap.containsKey(klass)) {
            return klass.cast(mServiceMap.get(klass));
        }
        mLogger.log(Level.SEVERE, klass + " - class not registered");
        return null;
    }

    /**
     * check if a service class has previously been registered
     *
     * @param klass to check registry for
     * @return true, is already mapped, otherwise false
     */
    public <T extends DHDPService> boolean containsService(Class<T> klass) {
        return mServiceMap.containsKey(klass);
    }
}
