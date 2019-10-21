package com.devhunter.dhdp;

import com.devhunter.dhdp.fieldnotes.service.FieldNoteService;
import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;
import com.devhunter.dhdp.services.CodecService;
import com.devhunter.dhdp.services.MySqlService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * Runs on DHDP start.
 * Services must be started in a particular order.
 * 1) Create the DHDPServiceRegistry - all services will have a referenced added to the registry
 * 2) Create the DHDPServices - these services can be used by the app specific services and
 * include common functionality that may be used by more than one app service
 * 3) Create the App Services - the services are specific to the Client App utilizing DHDP. They
 * hold information about how to connect to database, and how to process the data received in a
 * request
 * 4) Create the DHDPWorkflowHandler -  The Workflow handler creates each app specific Client Workflow.
 * The workflows are all created with a reference to the ServiceRegistry so they can resolve whichever
 * DHDP services and app services that they require to complete their tasks.
 * 5) Each Client spins off a new Thread to allow multi-client interaction.
 */
public class Main {
    private static Logger mLogger = Logger.getLogger(Main.class.getName());
    private static final int PORT = 8080;

    public static void main(String[] args) {
        // Register services on startup
        DHDPServiceRegistry registry = new DHDPServiceRegistry();
        // each service is created with the registry, so it can register itself
        // create DHDP services
        CodecService.initService(registry);
        MySqlService.initService(registry);
        // create client services
        FieldNoteService.initService(registry);

        // Create the Workflow handler
        DHDPWorkflowHandler handler = new DHDPWorkflowHandler(registry);

        ServerSocket serverSocket;
        Socket socket;
        try {
            serverSocket = new ServerSocket(PORT);
            mLogger.info("Listening on port " + String.valueOf(PORT));

            //noinspection InfiniteLoopStatement
            while (true) {
                socket = serverSocket.accept();
                // process the request on a new Thread
                new DHDPThread(registry, handler, socket).run();
            }
        } catch (IOException e) {
            mLogger.severe("Caught ServerSocketException");
        }
    }
}
