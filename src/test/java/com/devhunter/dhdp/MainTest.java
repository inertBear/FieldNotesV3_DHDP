package com.devhunter.dhdp;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainTest {
    private static JsonParser mJsonParser = new JsonParser();

    /**
     * Test connection to ServerSocket @ localhost
     */
    @Test
    public void testHttpGetWithJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("PING", "Tester");
        jsonObject.put("AGE", "47");
        jsonObject.put("ADDRESS", "123 This rd");

        JSONObject postResponse = mJsonParser.createHttpRequest("http://localhost:8080", "GET", jsonObject);
        assertEquals("success", postResponse.getString("status"));
        assertEquals("PING", postResponse.getString("message"));
    }

    /**
     * Test connection to ServerSocket @ localhost
     */
    @Test
    public void testHttpPostWithJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("PING", "Tester");
        jsonObject.put("AGE", "47");
        jsonObject.put("ADDRESS", "123 This rd");

        JSONObject postResponse = mJsonParser.createHttpRequest("http://localhost:8080", "POST", jsonObject);
        assertEquals("success", postResponse.getString("status"));
        assertEquals("PING", postResponse.getString("message"));
    }
}