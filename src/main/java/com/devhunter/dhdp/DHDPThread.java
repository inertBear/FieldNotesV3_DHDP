package com.devhunter.dhdp;

import com.devhunter.DHDPConnector4J.*;
import com.devhunter.dhdp.infrastructure.DHDPWorkflow;
import com.devhunter.dhdp.infrastructure.DHServiceRegistry;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Processes client requests on a new thread
 */
public class DHDPThread extends Thread {
    private Logger mLogger = Logger.getLogger(DHDPThread.class.getName());
    private DHDPWorkflowHandler mHandler;
    private Socket mSocket;

    public DHDPThread(DHServiceRegistry registry, Socket clientSocket) {
        mHandler = new DHDPWorkflowHandler(registry);
        mSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            // get the request from HTTP request
            DHDPRequest request = getJsonFromHttpRequest(mSocket);
            if (request != null) {
                mLogger.info(request.toString());

                // get the workflow that will process the request
                DHDPWorkflow workflow = mHandler.getWorkflow(request.getHeader());

                // process the request
                DHDPResponse response = workflow.process(request);
                mLogger.info(response.toString());

                sendProcessingComplete(response);
            } else {
                sendFailure();
            }
        } catch (IOException e) {
            mLogger.info(e.toString());
        }
    }

    /**
     * retrieves the JSON request from the HTTP Request
     *
     * @param clientSocket to read from
     * @return request as a JSONObject
     * @throws IOException if request cannot be read
     */
    private DHDPRequest getJsonFromHttpRequest(Socket clientSocket) throws IOException {
        //read the HTTP request from the client socket
        InputStreamReader isr = new InputStreamReader(clientSocket.getInputStream());

        // get the params from the request
        BufferedReader br = new BufferedReader(isr);

        // read the lines until we find the POST or GET data
        String line = br.readLine();
        while (!line.isEmpty()) {
            mLogger.log(Level.INFO, line);
            if (line.startsWith("GET") || line.startsWith("POST")) {
                // get the encoded json string
                line = line.substring(line.indexOf("?"), line.lastIndexOf(" "));

                JSONObject request = decode(line);

                //convert to DHDPRequest
                DHDPHeader header = DHDPHeader.newBuilder()
                        .setCreator(request.getString(DHDPHeader.CREATOR_KEY))
                        .setRequestType(request.getEnum(DHDPRequestType.class, DHDPHeader.REQUEST_TYPE_KEY))
                        .setOrganization(request.getEnum(DHDPOrganization.class, DHDPHeader.ORGANIZATION_KEY))
                        .setOriginator(request.getEnum(DHDPEntity.class, DHDPHeader.ORIGINATOR_KEY))
                        .setRecipient(request.getEnum(DHDPEntity.class, DHDPHeader.RECIPIENT_KEY))
                        .build();

                // remove header values from request
                request.remove(DHDPHeader.CREATOR_KEY);
                request.remove(DHDPHeader.REQUEST_TYPE_KEY);
                request.remove(DHDPHeader.ORGANIZATION_KEY);
                request.remove(DHDPHeader.ORIGINATOR_KEY);
                request.remove(DHDPHeader.RECIPIENT_KEY);

                // put the rest of the values in the body
                Map<String, Object> bodyMap = new HashMap<>();
                for (String each : request.keySet()) {
                    bodyMap.put(each, request.get(each));
                }

                // create DHDPRequest
                return DHDPRequest.newBuilder()
                        .setHeader(header)
                        .setBody(new DHDPRequestBody(bodyMap))
                        .build();
            } else {
                // get the next line
                line = br.readLine();
            }
        }
        mLogger.log(Level.INFO, "Rx empty HTTP Request");
        return null;
    }

    private void sendProcessingComplete(DHDPResponse response) {
        String httpResponse = "HTTP/1.1 200 OK\r\n\r\n";
        try {
            mSocket.getOutputStream().write(httpResponse.getBytes(StandardCharsets.UTF_8));

            // encode response
            String encodedResult = encode(response.toString());
            // write to client
            mSocket.getOutputStream().write(encodedResult.getBytes(StandardCharsets.UTF_8));

            mSocket.getOutputStream().flush();
            mSocket.getOutputStream().close();
        } catch (IOException e) {
            mLogger.severe((e.toString()));
        }
    }

    private void sendFailure() {
        String httpResponse = "HTTP/1.1 200 OK\r\n\r\n";
        try {
            mSocket.getOutputStream().write(httpResponse.getBytes(StandardCharsets.UTF_8));

            // create test response
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("STATUS", "FAILURE");
            jsonResponse.put("MESSAGE", "DHDP did not received a valid request");
            jsonResponse.put("TIMESTAMP", LocalDateTime.now());
            jsonResponse.put("RESULT", "no results");
            mLogger.info("LocalDHDP Tx response: " + jsonResponse.toString());

            // send test response
            String encodedResult = encode(jsonResponse.toString());
            mSocket.getOutputStream().write(encodedResult.getBytes(StandardCharsets.UTF_8));

            mSocket.getOutputStream().flush();
            mSocket.getOutputStream().close();
        } catch (IOException e) {
            mLogger.severe((e.toString()));
        }
    }

    private void sendTestResponse() {
        // prepare test response
        String httpResponse = "HTTP/1.1 200 OK\r\n\r\n";
        try {
            mSocket.getOutputStream().write(httpResponse.getBytes(StandardCharsets.UTF_8));

            // create test response
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("STATUS", "SUCCESS");
            jsonResponse.put("MESSAGE", "WE DID IT");
            jsonResponse.put("TIMESTAMP", LocalDateTime.now());
            jsonResponse.put("RESULT", "Results are overrated");
            mLogger.info("LocalDHDP Tx response: " + jsonResponse.toString());

            // send test response
            String encodedResult = encode(jsonResponse.toString());
            mSocket.getOutputStream().write(encodedResult.getBytes(StandardCharsets.UTF_8));

            mSocket.getOutputStream().flush();
            mSocket.getOutputStream().close();
        } catch (IOException e) {
            mLogger.severe((e.toString()));
        }
    }

    /**
     * Encode a String to BASE64
     *
     * @param value to encode
     * @return encoded value
     */
    private String encode(String value) {
        byte[] encodedBytes = Base64.encodeBase64(value.getBytes());
        return new String(encodedBytes);
    }

    /**
     * Decode a String to JSONObject
     *
     * @param value to decode
     * @return decoded value
     */
    private JSONObject decode(String value) {
        // decode
        byte[] decodedBytes = Base64.decodeBase64(value);
        // convert to JSONObject
        return new JSONObject(new String(decodedBytes));
    }

    /**
     * private class used to build the Request body for easy processing
     */
    private class DHDPRequestBody extends DHDPBody {

        DHDPRequestBody(Map<String, Object> bodyMap) throws IllegalArgumentException {
            super(bodyMap);
        }
    }
}
