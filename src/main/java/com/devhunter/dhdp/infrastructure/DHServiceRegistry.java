package com.devhunter.dhdp.infrastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DHServiceRegistry holds instances of service so that they can be retrieved and used by
 * anything that has access to the DHServiceRegistry
 *
 * 1) a specific service class can only be registered once
 * 2) services are resolved by class
 *
 * based on "Joshua Bloch's typesafe hetereogeneous container pattern"
 *
 */
public class DHServiceRegistry{
    private Logger mLogger = Logger.getLogger(DHServiceRegistry.class.getName());
    private Map<Class<?>, Object> serviceMap = new HashMap<>();

    /**
     * adds a new service to the DHServiceRegistry
     * If the service class is already registered, does nothing
     * NOTE: will NOT overwrite previously added services
     *
     * @param klass the Class of the service to register
     * @param service the concrete service being registered
     */
    public <T extends DHService>  void register(Class<T> klass, T service) {
        // check if class is already registered
        if (serviceMap.containsKey(klass)) {
            mLogger.log(Level.INFO, klass + " - class already registered");
            // do nothing
        } else {
            serviceMap.put(klass, service);
            mLogger.log(Level.INFO, klass + " - class registered");
        }
    }

    /**
     * retrieves a service from the DHServiceRegistry by class
     * If the name is not contained in the registry, returns null
     *
     * @param klass to retrieve from the registry
     * @return T the registered service, cast as {@param klass}
     */
    public <T extends DHService> T resolve(Class<T> klass) {
        // check if name is registered
        if (serviceMap.containsKey(klass)) {
            mLogger.log(Level.INFO, klass + " - class resolved");
            return klass.cast(serviceMap.get(klass));
        }
        mLogger.log(Level.SEVERE, klass + " - class not registered");
        return null;
    }
}
