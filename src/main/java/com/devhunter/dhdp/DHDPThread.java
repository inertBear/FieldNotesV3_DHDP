package com.devhunter.dhdp;

import com.devhunter.DHDPConnector4J.header.DHDPHeader;
import com.devhunter.DHDPConnector4J.request.DHDPRequest;
import com.devhunter.DHDPConnector4J.request.DHDPRequestType;
import com.devhunter.DHDPConnector4J.response.DHDPResponse;
import com.devhunter.DHDPConnector4J.response.DHDPResponseBody;
import com.devhunter.DHDPConnector4J.response.DHDPResponseType;
import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;
import com.devhunter.dhdp.infrastructure.DHDPWorkflow;
import com.devhunter.dhdp.services.CodecService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * Processes client requests on a new thread
 */
public class DHDPThread extends Thread {
    private Logger mLogger = Logger.getLogger(DHDPThread.class.getName());
    private DHDPWorkflowHandler mHandler;
    private CodecService mCodecService;
    private Socket mSocket;

    DHDPThread(final DHDPServiceRegistry registry, final DHDPWorkflowHandler handler, final Socket clientSocket) {
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
                // get the workflow that will process the request
                DHDPWorkflow workflow = mHandler.getWorkflow(request.getHeader());
                // process the request
                if (workflow != null) {
                    DHDPResponseBody responseBody = workflow.process(request);
                    DHDPResponse response = setHeader(request, responseBody);
                    // send response back to client
                    sendProcessingComplete(response);
                } else {
                    sendProcessingFailed("Invalid Originator");
                }
            } else {
                sendProcessingFailed("Invalid Request");
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
    private DHDPRequest getRequest(final Socket clientSocket) throws IOException {
        //read the HTTP request from the client socket
        InputStreamReader isr = new InputStreamReader(clientSocket.getInputStream());

        // get the params from the request
        BufferedReader br = new BufferedReader(isr);

        // read the lines until we find the POST or GET data
        String line = br.readLine();
        if (line != null) {
            while (!line.isEmpty()) {
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
        }
        mLogger.info("Rx empty HTTP Request");
        return null;
    }

    private DHDPResponse setHeader(final DHDPRequest request, final DHDPResponseBody responseBody) {
        DHDPHeader requestHeader = request.getHeader();
        // build Response Header (swap originator and recipient)
        return DHDPResponse.newBuilder()
                .setHeader(DHDPHeader.newBuilder()
                        .setCreator(requestHeader.getCreator())
                        .setRequestType(requestHeader.getRequestType())
                        .setOrganization(requestHeader.getOrganization())
                        .setOriginator(requestHeader.getRecipient())
                        .setRecipient(requestHeader.getOriginator())
                        .build())
                .setResponse(responseBody)
                .build();
    }

    /**
     * sends a processed Response to the Client
     *
     * @param response to encode and send
     */
    private void sendProcessingComplete(final DHDPResponse response) {
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
     */
    private void sendProcessingFailed(final String message) {
        String httpResponse = "HTTP/1.1 200 OK\r\n\r\n";
        try {
            mSocket.getOutputStream().write(httpResponse.getBytes(StandardCharsets.UTF_8));

            DHDPResponse response = buildFailureResponse(message);

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
     * @return response created from known request values
     */
    private DHDPResponse buildFailureResponse(final String message) {
        String unknownString = "UNKNOWN";
        String messageTemplate = "Unable to process request: ";

        return DHDPResponse.newBuilder()
                .setHeader(DHDPHeader.newBuilder()
                        .setCreator(unknownString)
                        .setOrganization(unknownString)
                        .setOriginator(unknownString)
                        .setRecipient(unknownString)
                        .setRequestType(DHDPRequestType.UNKNOWN)
                        .build())
                .setResponse(DHDPResponseBody.newBuilder()
                        .setResponseType(DHDPResponseType.FAILURE)
                        .setMessage(messageTemplate + message)
                        .setResults(null)
                        .build())
                .build();
    }
}