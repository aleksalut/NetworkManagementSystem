package com.networkmanagement.networkmanagementsystem.model;
import java.util.List;

public class Topology {
    private List<Device> devices;
    private List<Connection> connections;

    public Topology() {
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public void setConnections(List<Connection> connections) {
        this.connections = connections;
    }
}
