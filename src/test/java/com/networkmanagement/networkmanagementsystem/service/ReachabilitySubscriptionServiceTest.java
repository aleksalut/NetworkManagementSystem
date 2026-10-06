package com.networkmanagement.networkmanagementsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ReachabilitySubscriptionServiceTest {

    private TopologyService topologyService;
    private ReachabilitySubscriptionService subscriptionService;

    @BeforeEach
    void setUp() throws Exception {
        topologyService = new TopologyService();
        topologyService.loadTopology();
        subscriptionService = new ReachabilitySubscriptionService(topologyService);
    }

    @Test
    void sendsRemovedEventsWhenDeviceBecomesUnreachable() {
        RecordingSseEmitter emitter = new RecordingSseEmitter();
        subscriptionService.addSubscription(7, emitter, topologyService.getReachableDevices(7));
        emitter.events.clear();

        topologyService.updateDeviceActive(15, false);
        subscriptionService.updateSubscriptions();

        assertTrue(emitter.events.contains(Map.of("type", "REMOVED", "deviceId", 15)));
        assertTrue(emitter.events.contains(Map.of("type", "REMOVED", "deviceId", 19)));
    }

    private static class RecordingSseEmitter extends SseEmitter {

        private final List<Object> events = new ArrayList<>();

        @Override
        public void send(Object object) throws IOException {
            events.add(object);
        }
    }
}
