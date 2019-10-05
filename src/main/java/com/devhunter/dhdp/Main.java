package com.devhunter.dhdp;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static Logger mLogger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try {
            // all clients get in line, server only processes on at a a time
            final ServerSocket server = new ServerSocket(8080);
            System.out.println("Listening for connection on port " + server.getLocalPort());
            // listen forever
            while (true) {
                try (Socket clientSocket = server.accept()) {
                    JSONObject requestJson = getJsonFromHttpRequest(clientSocket);

                    // do things like: decided which Customer base it is.
                    // send JSON to Service

                    //----------------- FOR TEST ------------------------
                    //prepare HTTP response
                    String httpResponse = "HTTP/1.1 200 OK\r\n\r\n";
                    clientSocket.getOutputStream().write(httpResponse.getBytes(StandardCharsets.UTF_8));

                    //send HTTP response to client
                    JSONObject jsonResponse = new JSONObject();
                    jsonResponse.put("status", "success");
                    jsonResponse.put("message", "PING");
                    new PrintWriter(clientSocket.getOutputStream(), true).println(jsonResponse);
                }
            }
        } catch (IOException e) {
            mLogger.log(Level.SEVERE, "Caught ServerSocketException");
        }
    }

    private static JSONObject getJsonFromHttpRequest(Socket clientSocket) throws IOException {
        //read the HTTP request from the client socket
        InputStreamReader isr = new InputStreamReader(clientSocket.getInputStream());

        // get the params from the request
        BufferedReader br = new BufferedReader(isr);

        // read the lines until we find the POST or GET data
        String line = br.readLine();
        while (!line.isEmpty()) {
            mLogger.log(Level.INFO, line);
            if (line.startsWith("GET") || line.startsWith("POST")){
                // get the encoded json string
                line = line.substring(line.indexOf("?"), line.lastIndexOf(" "));
                // decode
                byte[] decodedBytes = Base64.decodeBase64(line);
                // convert to JSONObject
                return new JSONObject(new String(decodedBytes));
            } else {
                // get the next line
                line = br.readLine();
            }
        }
        mLogger.log(Level.INFO, "End of HTTP REQUEST");
        return null;
    }
}
