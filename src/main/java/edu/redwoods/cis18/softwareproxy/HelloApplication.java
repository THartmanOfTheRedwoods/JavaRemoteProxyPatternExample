package edu.redwoods.cis18.softwareproxy;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // Start the server
        SoftwareMonitorImpl server = new SoftwareMonitorImpl();
        int port = 8080;
        new Thread(() -> server.startServer(port)).start();

        // Use the proxy client to interact with the server
        SoftwareMonitor proxy = new SoftwareMonitorProxy("http://localhost:8080/softwareMonitor");

        String version = proxy.getVersion("AppA");
        System.out.println("Version of AppA: " + version);
        version = proxy.getVersion("AppB");
        System.out.println("Version of AppB: " + version);
        version = proxy.getVersion("AppC");
        System.out.println("Version of AppC: " + version);

        // boolean updateResult = proxy.triggerUpdate("AppA", "1.1.0");
        // System.out.println("Update result: " + updateResult);
        //launch();
    }
}