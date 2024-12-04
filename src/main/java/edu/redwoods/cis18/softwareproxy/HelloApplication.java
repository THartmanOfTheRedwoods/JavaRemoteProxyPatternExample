package edu.redwoods.cis18.softwareproxy;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private static SoftwareMonitorImpl server; // Usually in a different process on a differnt machine
    private static Thread serverThread; // Thread we're using to separate server from the GUI thread since I combined it

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        // Set a close request handler
        stage.setOnCloseRequest(event -> {
            System.out.println("Application is closing...");
            if(server != null) { server.stopServrer(); }

            if (serverThread != null) {
                System.out.println("Interrupting server thread...");
                serverThread.interrupt();
                try {
                    System.out.println("Waiting for server thread to exit...");
                    serverThread.join(5000);
                } catch (InterruptedException e) {
                    System.out.println("Failed to kill remote proxy server thread.");
                    throw new RuntimeException(e);
                }
                System.out.println("Goodbye!");
            }
        });
    }

    public static void main(String[] args) {
        // Start the server
        server = new SoftwareMonitorImpl();
        int port = 8080;
        serverThread = new Thread(() -> server.startServer(port));
        serverThread.start();

        // Launch the JavaFX Application
        launch();
    }
}