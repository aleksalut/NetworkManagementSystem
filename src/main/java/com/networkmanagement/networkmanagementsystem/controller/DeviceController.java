package com.networkmanagement.networkmanagementsystem.controller;

import com.networkmanagement.networkmanagementsystem.dto.DeviceUpdateRequest;
import com.networkmanagement.networkmanagementsystem.model.Device;
import com.networkmanagement.networkmanagementsystem.service.ReachabilitySubscriptionService;
import com.networkmanagement.networkmanagementsystem.service.TopologyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Set;

@RestController
public class DeviceController {

    private final TopologyService topologyService;
    private final ReachabilitySubscriptionService subscriptionService;

    public DeviceController(TopologyService topologyService,
                            ReachabilitySubscriptionService subscriptionService) {
        this.topologyService = topologyService;
        this.subscriptionService = subscriptionService;
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
        subscriptionService.updateSubscriptions();
        return updatedDevice;
    }

    @GetMapping("/devices/{id}/reachable-devices")
    public SseEmitter getReachableDevices(@PathVariable int id) {
        if (!topologyService.hasDevice(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found");
        }
        SseEmitter emitter = new SseEmitter(0L);

        Set<Integer> reachableDevices = topologyService.getReachableDevices(id);
        subscriptionService.addSubscription(id, emitter, reachableDevices);
        return emitter;
    }
}


