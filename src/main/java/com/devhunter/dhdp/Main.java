package com.devhunter.dhdp;

import com.devhunter.dhdp.infrastructure.DHServiceRegistry;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static Logger mLogger = Logger.getLogger(Main.class.getName());
    private static final int PORT = 8080;

    public static void main(String[] args) {
        // Register services on startup
        DHServiceRegistry registry = DHServiceRegistry.getInstance();

        ServerSocket serverSocket;
        Socket socket;
        try {
            serverSocket = new ServerSocket(PORT);
            mLogger.info("Listening on port " + String.valueOf(PORT));

            //noinspection InfiniteLoopStatement
            while (true) {
                socket = serverSocket.accept();
                // process the request on a new Thread
                new DHDPThread(registry, socket).run();
            }
        } catch (IOException e) {
            mLogger.log(Level.SEVERE, "Caught ServerSocketException");
        }
    }
}
