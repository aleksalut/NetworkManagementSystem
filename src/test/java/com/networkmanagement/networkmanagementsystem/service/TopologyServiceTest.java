package com.networkmanagement.networkmanagementsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TopologyServiceTest {

    private TopologyService topologyService;

    @BeforeEach
    void setUp() throws Exception {
        topologyService = new TopologyService();
        topologyService.loadTopology();
    }

    @Test
    void allActiveDevicesAreReachableFromLublin() {
        Set<Integer> expected = Set.of(0, 1, 2, 3, 4, 5, 6, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19);

        assertEquals(expected, topologyService.getReachableDevices(7));
    }

    @Test
    void turningOffKielceRemovesKielceAndItsDownstreamDevicesFromLublin() {
        topologyService.updateDeviceActive(15, false);

        Set<Integer> expected = Set.of(0, 1, 2, 3, 4, 5, 6, 8, 9, 10, 11, 12, 13, 14);

        assertEquals(expected, topologyService.getReachableDevices(7));
    }

    @Test
    void turningOffWroclawRemovesOnlyWroclawFromRadomReachability() {
        topologyService.updateDeviceActive(2, false);

        Set<Integer> expected = Set.of(0, 1, 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 14, 15, 16, 17, 18, 19);

        assertEquals(expected, topologyService.getReachableDevices(12));
    }

    @Test
    void turningOffTorunRemovesOnlyTorunFromLublinReachability() {
        topologyService.updateDeviceActive(13, false);

        Set<Integer> expected = Set.of(0, 1, 2, 3, 4, 5, 6, 8, 9, 10, 11, 12, 14, 15, 16, 17, 18, 19);

        assertEquals(expected, topologyService.getReachableDevices(7));
    }

    @Test
    void turningOffSosnowiecMakesGdanskReachabilityEmpty() {
        topologyService.updateDeviceActive(14, false);

        assertEquals(Set.of(), topologyService.getReachableDevices(4));
    }
}
