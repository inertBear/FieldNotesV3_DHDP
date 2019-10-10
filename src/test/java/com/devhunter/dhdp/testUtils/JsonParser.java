package com.devhunter.dhdp.testUtils;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class JsonParser {

    private Logger mLogger = Logger.getLogger(JsonParser.class.getName());

    public JSONObject createHttpRequest(String url, JSONObject request) throws JSONException {
        JSONObject response = null;
        InputStream inputStream;

        String encodedString = encode(request.toString());
        try {
            // Create Request
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost(url + encodedString);

            // Execute Request
            HttpResponse httpResponse = httpClient.execute(httpPost);

            // Retrieve Response
            HttpEntity httpEntity = httpResponse.getEntity();
            inputStream = httpEntity.getContent();

            // Read Response
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream, StandardCharsets.ISO_8859_1), 8);
            StringBuilder responseStringBuilder = new StringBuilder();
            String strLine;
            while ((strLine = reader.readLine()) != null) {
                responseStringBuilder.append(strLine).append("\n");
            }
            inputStream.close();

            // parse the string into JSON object
            response = new JSONObject(responseStringBuilder.toString());
        } catch (IOException | JSONException e) {
            mLogger.severe(e.toString());
        }
        mLogger.info("Response received: " + response.toString());
        return response;
    }


    public static String encode(String encodeMe) {
        byte[] encodedBytes = Base64.encodeBase64(encodeMe.getBytes());
        return new String(encodedBytes);
    }
}