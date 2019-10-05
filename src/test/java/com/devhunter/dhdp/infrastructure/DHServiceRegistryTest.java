package com.devhunter.dhdp.infrastructure;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DHServiceRegistryTest {
    public DHServiceRegistry mServiceRegistry;

    @Before
    public void setUp() {
        mServiceRegistry = new DHServiceRegistry();
    }

    /**
     * test to register a service
     */
    @Test
    public void registerAndResolveTestService() {
        // register
        DHDummyService dummyService = new DHDummyService("dummyService");
        mServiceRegistry.register(DHDummyService.class, dummyService);
        // resolve
        DHDummyService resolvedService = mServiceRegistry.resolve(DHDummyService.class);
        //assert
        assertEquals(dummyService.getName(), resolvedService.getName());
        // use resolved service
        assertEquals("Something, Something, Dark Side",  resolvedService.tellMeSomething());
    }

    /**
     * test to register a service with a name that is already registered
     *
     */
    @Test
    public void registerAndResolveServiceThatIsAlreadyRegistered() {
        //register
        DHDummyService dummyService1 = new DHDummyService("dummyService1");
        DHDummyService dummyService2 = new DHDummyService("dummyService2");
        mServiceRegistry.register(DHDummyService.class, dummyService1);
        mServiceRegistry.register(DHDummyService.class, dummyService2);
        // resolve
        DHDummyService resolvedService = mServiceRegistry.resolve(DHDummyService.class);
        //assert
        assertEquals(dummyService1.getName(), resolvedService.getName());
    }

    /**
     * test to resolve a registered service by Service class
     */
    @Test
    public void resolveByClass() {
        DHDummyService dummyService = new DHDummyService("dummyService");
        mServiceRegistry.register(DHDummyService.class, dummyService);
        DHDummyService resolvedService = mServiceRegistry.resolve(DHDummyService.class);
        assertEquals(dummyService.getName(), resolvedService.getName());
    }

    /**
     * test to resolve an unregistered service
     */
    @Test
    public void resolveUnregisteredTestService() {
        assertNull(mServiceRegistry.resolve(DHDummyService.class));
    }
}