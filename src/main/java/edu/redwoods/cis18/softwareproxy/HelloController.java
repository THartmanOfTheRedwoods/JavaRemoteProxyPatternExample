package edu.redwoods.cis18.softwareproxy;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        // Use the proxy client to interact with the server
        SoftwareMonitor proxy = new SoftwareMonitorProxy("http://localhost:8080/softwareMonitor");

        String version = "AppA: %s%n";
        version = String.format(version, proxy.getVersion("AppA"));
        version += "AppB: %s%n";
        version = String.format(version, proxy.getVersion("AppB"));
        version += "AppC: %s%n";
        version = String.format(version, proxy.getVersion("AppC"));

        System.out.printf("Versions:%n%s", version);
        welcomeText.setText(version);

        // Uncomment if you want to test triggerUpdate proxy.
        // boolean updateResult = proxy.triggerUpdate("AppA", "1.1.0");
        // System.out.println("Update result: " + updateResult);
    }
}