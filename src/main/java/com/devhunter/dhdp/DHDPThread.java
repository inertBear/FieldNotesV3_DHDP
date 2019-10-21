package com.devhunter.dhdp;

import com.devhunter.DHDPConnector4J.*;
import com.devhunter.DHDPConnector4J.groups.DHDPEntity;
import com.devhunter.DHDPConnector4J.groups.DHDPOrganization;
import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;
import com.devhunter.dhdp.infrastructure.DHDPWorkflow;
import com.devhunter.dhdp.services.CodecService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.logging.Logger;

/**
 * Processes client requests on a new thread
 */
public class DHDPThread extends Thread {
    private Logger mLogger = Logger.getLogger(DHDPThread.class.getName());
    private DHDPWorkflowHandler mHandler;
    private CodecService mCodecService;
    private Socket mSocket;

    DHDPThread(DHDPServiceRegistry registry, DHDPWorkflowHandler handler, Socket clientSocket) {
        mCodecService = registry.resolve(CodecService.class);
        mHandler = handler;
        mSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            // get the request from HTTP request
            DHDPRequest request = getRequest(mSocket);
            if (request != null) {
                mLogger.info(request.toString());

                // get the workflow that will process the request
                DHDPWorkflow workflow = mHandler.getWorkflow(request.getHeader());

                // process the request
                DHDPResponse response = workflow.process(request);
                mLogger.info(response.toString());

                // send response back to client
                sendProcessingComplete(response);
            } else {
                sendProcessingFailed(request);
            }
        } catch (IOException e) {
            mLogger.info(e.toString());
        }
    }

    /**
     * retrieves the DHDPRequest from the HTTP Request
     *
     * @param clientSocket to read from
     * @return request from client
     * @throws IOException if request cannot be read
     */
    private DHDPRequest getRequest(Socket clientSocket) throws IOException {
        //read the HTTP request from the client socket
        InputStreamReader isr = new InputStreamReader(clientSocket.getInputStream());

        // get the params from the request
        BufferedReader br = new BufferedReader(isr);

        // read the lines until we find the POST or GET data
        String line = br.readLine();
        while (!line.isEmpty()) {
            mLogger.info(line);
            if (line.startsWith("GET") || line.startsWith("POST")) {
                // get the encoded json string
                line = line.substring(line.indexOf("?"), line.lastIndexOf(" "));

                // decode request
                return mCodecService.decode(line);
            } else {
                // get the next line
                line = br.readLine();
            }
        }
        mLogger.info("Rx empty HTTP Request");
        return null;
    }

    /**
     * sends a processed Response to the Client
     *
     * @param response to encode and send
     */
    private void sendProcessingComplete(DHDPResponse response) {
        String httpResponse = "HTTP/1.1 200 OK\r\n\r\n";
        try {
            mSocket.getOutputStream().write(httpResponse.getBytes(StandardCharsets.UTF_8));

            // encode response
            String encodedResult = mCodecService.encode(response);
            // write to client
            mSocket.getOutputStream().write(encodedResult.getBytes(StandardCharsets.UTF_8));

            mSocket.getOutputStream().flush();
            mSocket.getOutputStream().close();
        } catch (IOException e) {
            mLogger.severe(e.toString());
        }
    }

    /**
     * sends a failure response to the Cient
     *
     * @param request that failed to process
     */
    private void sendProcessingFailed(DHDPRequest request) {
        String httpResponse = "HTTP/1.1 200 OK\r\n\r\n";
        try {
            mSocket.getOutputStream().write(httpResponse.getBytes(StandardCharsets.UTF_8));

            DHDPResponse response = buildFailureResponse(request);
            mLogger.info("LocalDHDP Tx response: " + response.toString());

            // send response
            String encodedResult = mCodecService.encode(response);
            mSocket.getOutputStream().write(encodedResult.getBytes(StandardCharsets.UTF_8));

            mSocket.getOutputStream().flush();
            mSocket.getOutputStream().close();
        } catch (IOException e) {
            mLogger.severe(e.toString());
        }
    }

    /**
     * build failure response if the Request cannot be processed
     *
     * @param request from client
     * @return response created from known request values
     */
    private DHDPResponse buildFailureResponse(DHDPRequest request) {
        DHDPHeader requestHeader = request.getHeader();

        DHDPHeader.Builder headerBuilder = DHDPHeader.newBuilder();
        if (requestHeader != null) {
            headerBuilder.setCreator(requestHeader.getCreator())
                    .setOrganization(requestHeader.getOrganization())
                    .setOriginator(requestHeader.getRecipient())
                    .setRecipient(requestHeader.getOriginator())
                    .setRequestType(requestHeader.getRequestType());
        } else {
            headerBuilder.setCreator("UNKNOWN")
                    .setOrganization(DHDPOrganization.UNKNOWN)
                    .setOriginator(DHDPEntity.DHDP)
                    .setRecipient(DHDPEntity.UNKNOWN)
                    .setRequestType(DHDPRequestType.UNKNOWN);
        }

        return DHDPResponse.newBuilder()
                .setHeader(headerBuilder.build())
                .setStatus(DHDPResponseType.FAILURE)
                .setMessage("DHDP Did not receive  valid request")
                .setTimestamp(LocalDateTime.now())
                .setResults(null)
                .build();
    }
}
