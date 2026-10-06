import { useEffect, useState } from 'react'

const deviceIds = Array.from({ length: 20 }, (_, index) => index)

function ReachabilityMonitor({ selectedDeviceId, setSelectedDeviceId }) {
  const [monitoringId, setMonitoringId] = useState(null)
  const [reachableDevices, setReachableDevices] = useState([])
  const [connectionStatus, setConnectionStatus] = useState('Disconnected')

  function startMonitoring() {
    setMonitoringId(selectedDeviceId)
  }

  return (
    <section className="card">
      <div className="section-heading">
        <div>
          <h2>Reachability monitor</h2>
        </div>
        <span className={`status ${connectionStatus.toLowerCase()}`}>
          {connectionStatus}
        </span>
      </div>

      <label htmlFor="monitor-device">Device to monitor</label>
      <div className="control-row">
        <select
          id="monitor-device"
          value={selectedDeviceId}
          onChange={(event) => setSelectedDeviceId(Number(event.target.value))}
        >
          {deviceIds.map((id) => (
            <option value={id} key={id}>Device {id}</option>
          ))}
        </select>
        <button onClick={startMonitoring}>Start monitoring</button>
      </div>

      <p className="supporting-text">
        {monitoringId === null
          ? 'Choose a device to start listening for reachability changes.'
          : `Monitoring device ${monitoringId}`}
      </p>

      <div className="reachable-list">
        <h3>Reachable device IDs</h3>
        {reachableDevices.length === 0 ? (
          <p className="empty-state">No reachable devices reported.</p>
        ) : (
          <div className="device-chips">
            {reachableDevices.map((id) => <span className="chip" key={id}>{id}</span>)}
          </div>
        )}
      </div>

      <ReachabilityConnection
        monitoringId={monitoringId}
        setReachableDevices={setReachableDevices}
        setConnectionStatus={setConnectionStatus}
      />
    </section>
  )
}

function ReachabilityConnection({ monitoringId, setReachableDevices, setConnectionStatus }) {
  useEffect(() => {
    if (monitoringId === null) {
      setConnectionStatus('Disconnected')
      setReachableDevices([])
      return undefined
    }

    const eventSource = new EventSource(`/devices/${monitoringId}/reachable-devices`)
    setConnectionStatus('Connecting')

    eventSource.onopen = () => setConnectionStatus('Connected')
    eventSource.onmessage = (event) => {
      const message = JSON.parse(event.data)

      if (message.type === 'INITIAL_STATE') {
        setReachableDevices(message.deviceIds)
      }

      if (message.type === 'ADDED') {
        setReachableDevices((current) => [...new Set([...current, message.deviceId])])
      }

      if (message.type === 'REMOVED') {
        setReachableDevices((current) => current.filter((id) => id !== message.deviceId))
      }
    }

    eventSource.onerror = () => setConnectionStatus('Disconnected')

    return () => {
      eventSource.close()
      setConnectionStatus('Disconnected')
    }
  }, [monitoringId, setConnectionStatus, setReachableDevices])

  return null
}

function DeviceControl() {
  const [deviceId, setDeviceId] = useState(0)
  const [active, setActive] = useState(true)
  const [message, setMessage] = useState('')

  async function updateDevice(event) {
    event.preventDefault()
    setMessage('Updating...')

    try {
      const response = await fetch(`/devices/${deviceId}`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ active }),
      })

      if (!response.ok) {
        throw new Error(`Request failed with status ${response.status}`)
      }

      setMessage(`Device ${deviceId} is now ${active ? 'active' : 'inactive'}.`)
    } catch (error) {
      setMessage(`Update failed: ${error.message}`)
    }
  }

  return (
    <section className="card">
      <h2>Update device status</h2>
      <form onSubmit={updateDevice}>
        <label htmlFor="control-device">Device ID</label>
        <input
          id="control-device"
          type="number"
          min="0"
          max="19"
          value={deviceId}
          onChange={(event) => setDeviceId(Number(event.target.value))}
        />

        <label htmlFor="active-state">New state</label>
        <select
          id="active-state"
          value={active ? 'active' : 'inactive'}
          onChange={(event) => setActive(event.target.value === 'active')}
        >
          <option value="active">Active</option>
          <option value="inactive">Inactive</option>
        </select>

        <button type="submit">Update device</button>
      </form>
      {message && <p className="result-message">{message}</p>}
    </section>
  )
}

function App() {
  const [selectedDeviceId, setSelectedDeviceId] = useState(7)

  return (
    <main className="app-shell">
      <header className="page-header">
        <h1>Network Management System</h1>
        <p>Monitor device reachability and update device status in real time.</p>
      </header>

      <div className="dashboard-grid">
        <ReachabilityMonitor
          selectedDeviceId={selectedDeviceId}
          setSelectedDeviceId={setSelectedDeviceId}
        />
        <DeviceControl />
      </div>
    </main>
  )
}

export default App
