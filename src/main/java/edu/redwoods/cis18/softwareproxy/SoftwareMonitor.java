package edu.redwoods.cis18.softwareproxy;

public interface SoftwareMonitor {
    String getVersion(String softwareName);
    boolean triggerUpdate(String softwareName, String newVersion);
}