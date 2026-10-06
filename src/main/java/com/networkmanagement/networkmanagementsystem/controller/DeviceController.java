package com.networkmanagement.networkmanagementsystem.controller;

import com.networkmanagement.networkmanagementsystem.dto.DeviceUpdateRequest;
import com.networkmanagement.networkmanagementsystem.model.Device;
import com.networkmanagement.networkmanagementsystem.service.TopologyService;
import com.networkmanagement.networkmanagementsystem.subscription.ReachabilitySubscription;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
public class DeviceController {

    private final TopologyService topologyService;
    private final List<ReachabilitySubscription> subscriptions = new CopyOnWriteArrayList<>();

    public DeviceController(TopologyService topologyService) {
        this.topologyService = topologyService;
    }

    @PatchMapping("/devices/{id}")
    public Device updateDevice(@PathVariable int id,
                               @RequestBody(required = false) DeviceUpdateRequest request) {
        if (!topologyService.hasDevice(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found");
        }
        if (request == null || request.getActive() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "active is required");
        }
        Device updatedDevice = topologyService.updateDeviceActive(id, request.getActive());
        updateSubscriptions();
        return updatedDevice;
    }

    private void updateSubscriptions() {
        for (ReachabilitySubscription subscription : subscriptions) {
            Set<Integer> oldReachable = subscription.getLastReachable();
            Set<Integer> newReachable = topologyService.getReachableDevices(subscription.getDeviceId());

            Set<Integer> removed = new HashSet<>(oldReachable);
            removed.removeAll(newReachable);

            Set<Integer> added = new HashSet<>(newReachable);
            added.removeAll(oldReachable);
            try {
                for (int deviceId : removed) {
                    Map<String, Object> event = new HashMap<>();
                    event.put("type", "REMOVED");
                    event.put("deviceId", deviceId);

                    subscription.getEmitter().send(event);
                }

                for (int deviceId : added) {
                    Map<String, Object> event = new HashMap<>();
                    event.put("type", "ADDED");
                    event.put("deviceId", deviceId);

                    subscription.getEmitter().send(event);
                }

                subscription.setLastReachable(newReachable);
            } catch (Exception e) {
                subscription.getEmitter().completeWithError(e);
            }
        }
    }

    @GetMapping("/devices/{id}/reachable-devices")
    public SseEmitter getReachableDevices(@PathVariable int id) {
        if (!topologyService.hasDevice(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found");
        }
        SseEmitter emitter = new SseEmitter(0L);

        Set<Integer> reachableDevices = topologyService.getReachableDevices(id);
        ReachabilitySubscription subscription = new ReachabilitySubscription(id, emitter, reachableDevices);
        subscriptions.add(subscription);
        Runnable cleanup = () -> subscriptions.remove(subscription);
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(error -> cleanup.run());
        Map<String, Object> initialState = new HashMap<>();
        initialState.put("type", "INITIAL_STATE");
        initialState.put("deviceIds", reachableDevices);
        try {
            emitter.send(initialState);
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }
}


