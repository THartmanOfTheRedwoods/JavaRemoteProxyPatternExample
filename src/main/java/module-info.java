module edu.redwoods.cis18.softwareproxy {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires jdk.httpserver;


    opens edu.redwoods.cis18.softwareproxy to javafx.fxml;
    exports edu.redwoods.cis18.softwareproxy;
}