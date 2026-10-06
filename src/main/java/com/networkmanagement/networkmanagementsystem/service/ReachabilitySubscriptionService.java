package com.networkmanagement.networkmanagementsystem.service;

import com.networkmanagement.networkmanagementsystem.subscription.ReachabilitySubscription;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ReachabilitySubscriptionService {

    private final TopologyService topologyService;
    private final List<ReachabilitySubscription> subscriptions = new CopyOnWriteArrayList<>();

    public ReachabilitySubscriptionService(TopologyService topologyService) {
        this.topologyService = topologyService;
    }

    public void addSubscription(int deviceId, SseEmitter emitter, Set<Integer> reachableDevices) {
        ReachabilitySubscription subscription = new ReachabilitySubscription(deviceId, emitter, reachableDevices);
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
    }

    public void updateSubscriptions() {
        for (ReachabilitySubscription subscription : subscriptions) {
            Set<Integer> oldReachable = subscription.getLastReachable();
            Set<Integer> newReachable = topologyService.getReachableDevices(subscription.getDeviceId());

            Set<Integer> removed = new HashSet<>(oldReachable);
            removed.removeAll(newReachable);

            Set<Integer> added = new HashSet<>(newReachable);
            added.removeAll(oldReachable);
            try {
                sendEvents(subscription, removed, "REMOVED");
                sendEvents(subscription, added, "ADDED");
                subscription.setLastReachable(newReachable);
            } catch (Exception e) {
                subscription.getEmitter().completeWithError(e);
            }
        }
    }

    private void sendEvents(ReachabilitySubscription subscription,
                            Set<Integer> deviceIds,
                            String eventType) throws Exception {
        for (int deviceId : deviceIds) {
            Map<String, Object> event = new HashMap<>();
            event.put("type", eventType);
            event.put("deviceId", deviceId);
            subscription.getEmitter().send(event);
        }
    }
}
