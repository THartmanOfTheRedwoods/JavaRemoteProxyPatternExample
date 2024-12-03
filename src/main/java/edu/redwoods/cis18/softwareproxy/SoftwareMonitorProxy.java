package edu.redwoods.cis18.softwareproxy;

import com.google.gson.Gson;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

public class SoftwareMonitorProxy implements SoftwareMonitor {
    private final String serverUrl;
    private final Gson gson = new Gson();

    public SoftwareMonitorProxy(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    @Override
    public String getVersion(String softwareName) {
        String jsonRequest = gson.toJson(Map.of(
                "action", "getVersion",
                "softwareName", softwareName
        ));
        String jsonResponse = sendRequest(jsonRequest);
        Map<String, Object> response = gson.fromJson(jsonResponse, Map.class);
        return (String) response.get("result");
    }

    @Override
    public boolean triggerUpdate(String softwareName, String newVersion) {
        String jsonRequest = gson.toJson(Map.of(
                "action", "triggerUpdate",
                "softwareName", softwareName,
                "newVersion", newVersion
        ));
        String jsonResponse = sendRequest(jsonRequest);
        Map<String, Object> response = gson.fromJson(jsonResponse, Map.class);
        return (boolean) response.get("result");
    }

    private String sendRequest(String jsonRequest) {
        try {
            URL url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonRequest.getBytes());
            }

            try (Scanner scanner = new Scanner(connection.getInputStream())) {
                return scanner.useDelimiter("\\A").next();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to communicate with server", e);
        }
    }
}

