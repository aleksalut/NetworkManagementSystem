package com.networkmanagement.networkmanagementsystem.dto;

public class DeviceUpdateRequest {
    private Boolean active;

    public DeviceUpdateRequest(Boolean active) {
        this.active = active;
    }

    public DeviceUpdateRequest() {
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
