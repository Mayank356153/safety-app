import { useState } from 'react';
import LocationTracker from '../plugins/location-tracker';

export default function DebugPanel() {
  const [logs, setLogs] = useState([]);
  const [serverUrl, setServerUrl] = useState('https://safety-app-server-6p3a.onrender.com');
  const [userId, setUserId] = useState('');

  const addLog = (message) => {
    setLogs(prev => [...prev, `${new Date().toLocaleTimeString()}: ${message}`]);
  };

  const testServerConnection = async () => {
    addLog('Testing server connection...');
    try {
      const response = await fetch(`${serverUrl}/api/health`);
      const data = await response.json();
      addLog(`✅ Server connected: ${data.message}`);
    } catch (error) {
      addLog(`❌ Server connection failed: ${error.message}`);
    }
  };

  const testGetLocation = async () => {
    addLog('Getting current location...');
    try {
      const location = await LocationTracker.getCurrentLocation();
      addLog(`✅ Location: ${location.latitude}, ${location.longitude}`);
    } catch (error) {
      addLog(`❌ Location error: ${error.message}`);
    }
  };

  const testUpdateLocation = async () => {
    addLog('Testing location update to server...');
    try {
      const location = await LocationTracker.getCurrentLocation();
      addLog(`Got location: ${location.latitude}, ${location.longitude}`);

      const response = await fetch(`${serverUrl}/api/location/update`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId: userId || localStorage.getItem('userId'),
          latitude: location.latitude,
          longitude: location.longitude,
          accuracy: location.accuracy
        })
      });

      const data = await response.json();
      addLog(`✅ Server response: ${JSON.stringify(data)}`);
    } catch (error) {
      addLog(`❌ Update failed: ${error.message}`);
    }
  };

  const testStartService = async () => {
    addLog('Starting location service...');
    try {
      await LocationTracker.startLocationUpdates();
      addLog('✅ Location service started');
    } catch (error) {
      addLog(`❌ Service error: ${error.message}`);
    }
  };

  const checkSharedPrefs = () => {
    const stored = {
      userId: localStorage.getItem('userId'),
      serverUrl: localStorage.getItem('serverUrl'),
      userName: localStorage.getItem('userName'),
      userPhone: localStorage.getItem('userPhone')
    };
    addLog(`📦 Stored data: ${JSON.stringify(stored, null, 2)}`);
  };

  return (
    <div style={{ padding: 20 }}>
      <h2>🔧 Debug Panel</h2>

      <div style={{ marginBottom: 20 }}>
        <input
          type="text"
          value={serverUrl}
          onChange={(e) => setServerUrl(e.target.value)}
          placeholder="Server URL"
          style={{ width: '100%', padding: 10, marginBottom: 10 }}
        />
        <input
          type="text"
          value={userId}
          onChange={(e) => setUserId(e.target.value)}
          placeholder="User ID"
          style={{ width: '100%', padding: 10, marginBottom: 10 }}
        />
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: 10, marginBottom: 20 }}>
        <button onClick={testServerConnection} style={styles.button}>
          1. Test Server Connection
        </button>
        <button onClick={checkSharedPrefs} style={styles.button}>
          2. Check Stored Data
        </button>
        <button onClick={testGetLocation} style={styles.button}>
          3. Get Current Location
        </button>
        <button onClick={testUpdateLocation} style={styles.button}>
          4. Test Location Update
        </button>
        <button onClick={testStartService} style={styles.button}>
          5. Start Location Service
        </button>
        <button onClick={() => setLogs([])} style={{ ...styles.button, backgroundColor: '#f44336' }}>
          Clear Logs
        </button>
      </div>

      <div style={{
        backgroundColor: '#000',
        color: '#0f0',
        padding: 15,
        borderRadius: 8,
        fontFamily: 'monospace',
        fontSize: 12,
        maxHeight: 400,
        overflowY: 'auto'
      }}>
        {logs.map((log, i) => (
          <div key={i}>{log}</div>
        ))}
      </div>
    </div>
  );
}

const styles = {
  button: {
    padding: 12,
    backgroundColor: '#2196F3',
    color: 'white',
    border: 'none',
    borderRadius: 5,
    cursor: 'pointer',
    fontSize: 14
  }
};