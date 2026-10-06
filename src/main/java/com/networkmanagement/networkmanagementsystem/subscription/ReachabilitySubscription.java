package com.networkmanagement.networkmanagementsystem.subscription;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.Set;

public class ReachabilitySubscription {
    private final int deviceId;
    private final SseEmitter emitter;
    private Set<Integer> lastReachable;

    public ReachabilitySubscription(
            int deviceId,
            SseEmitter emitter,
            Set<Integer> lastReachable) {
        this.deviceId = deviceId;
        this.emitter = emitter;
        this.lastReachable = lastReachable;
    }

    public int getDeviceId(){
        return deviceId;
    }
    public SseEmitter getEmitter(){
        return emitter;
    }
    public Set<Integer> getLastReachable(){
        return lastReachable;
    }

    public void setLastReachable(Set<Integer> lastReachable){
        this.lastReachable = lastReachable;
    }
}
