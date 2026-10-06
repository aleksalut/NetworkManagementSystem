package com.networkmanagement.networkmanagementsystem.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.junit.jupiter.api.BeforeEach;
import com.networkmanagement.networkmanagementsystem.service.TopologyService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DeviceControllerApiTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {
        TopologyService topologyService = new TopologyService();
        topologyService.loadTopology();
        mockMvc = MockMvcBuilders
                .standaloneSetup(new DeviceController(topologyService))
                .build();
    }

    @Test
    void patchUpdatesDeviceActiveState() throws Exception {
        mockMvc.perform(patch("/devices/15")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(15))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void patchUnknownDeviceReturnsNotFound() throws Exception {
        mockMvc.perform(patch("/devices/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\":false}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchWithoutActiveReturnsBadRequest() throws Exception {
        mockMvc.perform(patch("/devices/15")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchWithNullActiveReturnsBadRequest() throws Exception {
        mockMvc.perform(patch("/devices/15")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\":null}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void reachabilityForUnknownDeviceReturnsNotFound() throws Exception {
        mockMvc.perform(get("/devices/999/reachable-devices"))
                .andExpect(status().isNotFound());
    }
}
