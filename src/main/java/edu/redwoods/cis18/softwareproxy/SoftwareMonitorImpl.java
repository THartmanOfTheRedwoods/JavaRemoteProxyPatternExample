package edu.redwoods.cis18.softwareproxy;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class SoftwareMonitorImpl implements SoftwareMonitor {
    private final Map<String, String> softwareVersions = new HashMap<>();
    private final Gson gson = new Gson();

    public SoftwareMonitorImpl() {
        // Initialize some dummy data
        softwareVersions.put("AppA", "1.0.0");
        softwareVersions.put("AppB", "2.3.1");
        softwareVersions.put("AppC", "0.0.1");
    }

    @Override
    public String getVersion(String softwareName) {
        return softwareVersions.getOrDefault(softwareName, "Unknown");
    }

    @Override
    public boolean triggerUpdate(String softwareName, String newVersion) {
        if (softwareVersions.containsKey(softwareName)) {
            softwareVersions.put(softwareName, newVersion);
            return true;
        }
        return false;
    }

    // Handle a JSON request and generate a JSON response
    public String handleRequest(String jsonRequest) {
        Map<String, Object> request = gson.fromJson(jsonRequest, Map.class);
        String action = (String) request.get("action");
        String softwareName = (String) request.get("softwareName");

        switch (action) {
            case "getVersion":
                String version = getVersion(softwareName);
                return gson.toJson(Map.of("result", version));
            case "triggerUpdate":
                String newVersion = (String) request.get("newVersion");
                boolean success = triggerUpdate(softwareName, newVersion);
                return gson.toJson(Map.of("result", success));
            default:
                return gson.toJson(Map.of("error", "Unknown action"));
        }
    }

    // Start the HTTP server
    public void startServer(int port) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/softwareMonitor", new SoftwareMonitorHandler());
            server.setExecutor(null); // Use the default executor
            server.start();
            System.out.println("Server started on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // HTTP handler for processing requests
    private class SoftwareMonitorHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                // Read the request body
                InputStream inputStream = exchange.getRequestBody();
                // The lambda function operator concatenates each line (line) to the accumulated string (acc),
                // effectively joining all lines into a single string. identity is what acc starts with.
                String jsonRequest = new BufferedReader(new InputStreamReader(inputStream))
                        .lines()
                        .reduce("", (acc, line) -> acc + line);

                // System.out.printf("~~~ %s ~~~%n", jsonRequest);
                // Process the request
                String jsonResponse = handleRequest(jsonRequest);

                // Send the response
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
                OutputStream outputStream = exchange.getResponseBody();
                outputStream.write(jsonResponse.getBytes());
                outputStream.close();
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }
}
