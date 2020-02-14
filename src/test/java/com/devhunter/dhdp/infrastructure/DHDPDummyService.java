package com.devhunter.dhdp.infrastructure;

/**
 * A service used to test the ServiceRegistry. It is NOT created via
 * the initService method ONLY because the test is more easily executed
 * when the service can be created and added to the registry ON DEMAND.
 * <p>
 * Usually a service would be created through the initService method so
 * no attempt to register a second instance on the service is made.
 */
public class DHDPDummyService extends DHDPService {

    DHDPDummyService(String name) {
        super(name);
    }

    public static void initService(DHDPServiceRegistry registry) {
        if (!registry.containsService(DHDPDummyService.class)) {
            registry.register(DHDPDummyService.class, new DHDPDummyService("DummyService"));
        }
    }

    String tellMeSomething() {
        return "Something, Something, Dark Side";
    }
}
