package com.devhunter.dhdp.infrastructure;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DHDPServiceRegistryTest {
    private DHDPServiceRegistry mServiceRegistry;

    @Before
    public void setUp() {
        mServiceRegistry = new DHDPServiceRegistry();
    }

    /**
     * test to register a service
     */
    @Test
    public void registerAndResolveTestService() {
        // register
        DHDPDummyService dummyService = new DHDPDummyService("Dummy Service");
        mServiceRegistry.register(DHDPDummyService.class, dummyService);
        // resolve
        DHDPDummyService resolvedService = mServiceRegistry.resolve(DHDPDummyService.class);
        //assert
        assertEquals(dummyService.getName(), resolvedService.getName());
        // use resolved service
        assertEquals("Something, Something, Dark Side", resolvedService.tellMeSomething());
    }

    /**
     * test to register a service with a name that is already registered
     */
    @Test
    public void registerAndResolveServiceThatIsAlreadyRegistered() {
        //register
        DHDPDummyService dummyService1 = new DHDPDummyService("dummyService1");
        DHDPDummyService dummyService2 = new DHDPDummyService("dummyService2");
        mServiceRegistry.register(DHDPDummyService.class, dummyService1);
        mServiceRegistry.register(DHDPDummyService.class, dummyService2);
        // resolve
        DHDPDummyService resolvedService = mServiceRegistry.resolve(DHDPDummyService.class);
        //assert
        assertEquals(dummyService1.getName(), resolvedService.getName());
    }

    /**
     * test to resolve a registered service by Service class
     */
    @Test
    public void resolveByClass() {
        DHDPDummyService dummyService = new DHDPDummyService("dummyService");
        mServiceRegistry.register(DHDPDummyService.class, dummyService);
        DHDPDummyService resolvedService = mServiceRegistry.resolve(DHDPDummyService.class);
        assertEquals(dummyService.getName(), resolvedService.getName());
    }

    /**
     * test to resolve an unregistered service
     */
    @Test
    public void resolveUnregisteredTestService() {
        assertNull(mServiceRegistry.resolve(DHDPDummyService.class));
    }

    /**
     * test that a service's existence can be checked
     */
    @Test
    public void containsServiceTest() {
        DHDPDummyService dummyService = new DHDPDummyService("dummyService");
        mServiceRegistry.register(DHDPDummyService.class, dummyService);
        assertTrue(mServiceRegistry.containsService(DHDPDummyService.class));
    }
}