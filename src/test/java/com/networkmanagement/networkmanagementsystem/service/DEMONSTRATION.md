# Demonstration Results

This document contains the results of the four demonstration scenarios required in the assignment.

Each scenario was executed separately with the application started in its initial state, where all devices are active.

The SSE connection was opened using the /devices/{id}/reachable-devices endpoint.  
The selected device was then disabled using PATCH /devices/{id}.

The actual SSE output observed during each scenario is included below.

## Scenario 1 – Lublin / Kielce

Subscribe to Lublin (device 7) and disable Kielce (device 15).

### SSE output
data:{"deviceIds":[0,1,2,3,4,5,6,8,9,10,11,12,13,14,15,16,17,18,19],"type":"INITIAL_STATE"}

data:{"type":"REMOVED","deviceId":15}

data:{"type":"REMOVED","deviceId":16}

data:{"type":"REMOVED","deviceId":17}

data:{"type":"REMOVED","deviceId":18}

data:{"type":"REMOVED","deviceId":19}


## Scenario 2 – Radom / Wroclaw

Subscribe to Radom (device 12) and disable Wroclaw (device 2).

### SSE output
data:{"deviceIds":[0,1,2,3,4,5,6,7,8,9,10,11,13,14,15,16,17,18,19],"type":"INITIAL_STATE"}

data:{"type":"REMOVED","deviceId":2}


## Scenario 3 – Lublin / Torun

Subscribe to Lublin (device 7) and disable Torun (device 13).

### SSE output
data:{"deviceIds":[0,1,2,3,4,5,6,8,9,10,11,12,13,14,15,16,17,18,19],"type":"INITIAL_STATE"}

data:{"type":"REMOVED","deviceId":13}


## Scenario 4 – Gdansk / Sosnowiec

Subscribe to Gdansk (device 4) and disable Sosnowiec (device 14).

### SSE output
data:{"deviceIds":[0,1,2,3,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19],"type":"INITIAL_STATE"}

data:{"type":"REMOVED","deviceId":0}

data:{"type":"REMOVED","deviceId":1}

data:{"type":"REMOVED","deviceId":2}

data:{"type":"REMOVED","deviceId":3}

data:{"type":"REMOVED","deviceId":5}

data:{"type":"REMOVED","deviceId":6}

data:{"type":"REMOVED","deviceId":7}

data:{"type":"REMOVED","deviceId":8}

data:{"type":"REMOVED","deviceId":9}

data:{"type":"REMOVED","deviceId":10}

data:{"type":"REMOVED","deviceId":11}

data:{"type":"REMOVED","deviceId":12}

data:{"type":"REMOVED","deviceId":13}

data:{"type":"REMOVED","deviceId":14}

data:{"type":"REMOVED","deviceId":15}

data:{"type":"REMOVED","deviceId":16}

data:{"type":"REMOVED","deviceId":17}

data:{"type":"REMOVED","deviceId":18}

data:{"type":"REMOVED","deviceId":19}