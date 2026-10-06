package com.networkmanagement.networkmanagementsystem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networkmanagement.networkmanagementsystem.model.Connection;
import com.networkmanagement.networkmanagementsystem.model.Device;
import com.networkmanagement.networkmanagementsystem.model.Topology;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

@Service
public class TopologyService {

    private final Map<Integer, Device> devices = new HashMap<>();
    private final Map<Integer, Set<Integer>> connections = new HashMap<>();

    @PostConstruct
    public void loadTopology() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        InputStream inputStream = getClass()
                .getClassLoader()
                .getResourceAsStream("topology.json");

        Topology topology = objectMapper.readValue(inputStream, Topology.class);

        for (Device device : topology.getDevices()) {
            devices.put(device.getId(), device);
            connections.put(device.getId(), new HashSet<>());
        }

        for (Connection connection : topology.getConnections()) {
            connections.get(connection.getFrom()).add(connection.getTo());
            connections.get(connection.getTo()).add(connection.getFrom());
        }
    }

    public boolean hasDevice(int id) {
        return devices.containsKey(id);
    }

    public Set<Integer> getReachableDevices(int startId) {
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new ArrayDeque<>();

        Device startDevice = devices.get(startId);

        if (!startDevice.isActive()) {
            return new HashSet<>();
        }
        visited.add(startId);
        queue.add(startId);

        while (!queue.isEmpty()) {
            int current = queue.poll();

            for (int neighbour : connections.get(current)) {
                Device neighbourDevice = devices.get(neighbour);

                if (neighbourDevice.isActive() && !visited.contains(neighbour)) {
                    visited.add(neighbour);
                    queue.add(neighbour);
                }
            }
        }
        visited.remove(startId);
        return visited;
    }

    public Device updateDeviceActive(int id, Boolean active) {
        Device device = devices.get(id);
        device.setActive(active);
        return device;
    }
}
