import { useState, useEffect } from 'react';
import LocationTracker from '../plugins/location-tracker';
import AppPreferences from '../plugins/app-preferences';

export default function ServiceStatus() {
  const [status, setStatus] = useState({});
  const [logs, setLogs] = useState([]);

  useEffect(() => {
    checkStatus();
    const interval = setInterval(checkStatus, 5000); // Check every 5 seconds
    return () => clearInterval(interval);
  }, []);

  const checkStatus = async () => {
  try {
    const settings = await AppPreferences.getLocationSettings();
    const serviceStatus = await LocationTracker.isServiceRunning();
    
    setStatus({
      userId: settings.userId,
      serverUrl: settings.serverUrl,
      serviceRunning: serviceStatus.isRunning,
      timestamp: new Date().toLocaleTimeString()
    });
  } catch (error) {
    console.error('Error checking status:', error);
  }
};

  const startService = async () => {
    addLog('Starting location service...');
    try {
      const userId = localStorage.getItem('userId');
      const serverUrl = localStorage.getItem('serverUrl');

      addLog(`User ID: ${userId}`);
      addLog(`Server URL: ${serverUrl}`);

      // Save to native storage
      await AppPreferences.saveLocationSettings({
        userId: userId,
        serverUrl: serverUrl
      });
      addLog('✅ Settings saved to native storage');

      // Start service
      const result = await LocationTracker.startLocationUpdates();
      addLog(`✅ Service started: ${JSON.stringify(result)}`);

      alert('Service started! Check Logcat for updates.');
    } catch (error) {
      addLog(`❌ Error: ${error.message}`);
    }
  };

  const stopService = async () => {
    addLog('Stopping service...');
    try {
      await LocationTracker.stopLocationUpdates();
      addLog('✅ Service stopped');
    } catch (error) {
      addLog(`❌ Error: ${error.message}`);
    }
  };

  const testLocationUpdate = async () => {
    addLog('Testing manual location update...');
    try {
      const location = await LocationTracker.getCurrentLocation();
      addLog(`📍 Location: ${location.latitude}, ${location.longitude}`);

      const serverUrl = localStorage.getItem('serverUrl');
      const userId = localStorage.getItem('userId');

      addLog(`Sending to: ${serverUrl}/api/location/update`);

      const response = await fetch(`${serverUrl}/api/location/update`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId: userId,
          latitude: location.latitude,
          longitude: location.longitude,
          accuracy: location.accuracy,
          timestamp: Date.now()
        })
      });

      const data = await response.json();
      addLog(`✅ Response: ${JSON.stringify(data)}`);
    } catch (error) {
      addLog(`❌ Error: ${error.message}`);
    }
  };

  const addLog = (msg) => {
    setLogs(prev => [...prev, `${new Date().toLocaleTimeString()}: ${msg}`]);
  };

  return (
    <div style={{ padding: 20 }}>
      <h2>📊 Service Status</h2>

      <div style={{
  padding: 15,
  backgroundColor: status.serviceRunning ? '#e8f5e9' : '#ffebee',
  borderRadius: 8,
  marginBottom: 20
}}>
  <div><strong>Service Status:</strong> {status.serviceRunning ? '🟢 Running' : '🔴 Stopped'}</div>
  <div><strong>User ID:</strong> {status.userId || 'Not set'}</div>
  <div><strong>Server:</strong> {status.serverUrl || 'Not set'}</div>
  <div><strong>Last Check:</strong> {status.timestamp}</div>
</div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: 10, marginBottom: 20 }}>
        <button onClick={startService} style={styles.button}>
          ▶️ Start Location Service
        </button>
        <button onClick={stopService} style={{ ...styles.button, backgroundColor: '#f44336' }}>
          ⏹️ Stop Location Service
        </button>
        <button onClick={testLocationUpdate} style={{ ...styles.button, backgroundColor: '#FF9800' }}>
          🧪 Test Manual Update
        </button>
        <button onClick={() => setLogs([])} style={{ ...styles.button, backgroundColor: '#9E9E9E' }}>
          🗑️ Clear Logs
        </button>
      </div>

      <h3>📝 Logs:</h3>
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
        {logs.length === 0 && <div>No logs yet...</div>}
        {logs.map((log, i) => (
          <div key={i}>{log}</div>
        ))}
      </div>

      <div style={{
        marginTop: 20,
        padding: 15,
        backgroundColor: '#fff3cd',
        borderRadius: 8
      }}>
        <h4>📌 How to Check Service:</h4>
        <ol style={{ margin: 0, paddingLeft: 20 }}>
          <li>Click "Start Location Service"</li>
          <li>Go to Android Studio → Logcat</li>
          <li>Filter by: <code>LocationUpdateService</code></li>
          <li>You should see location updates every 2 minutes</li>
          <li>Check your phone's notification - should see "Location Tracking Active"</li>
        </ol>
      </div>
    </div>
  );
}

const styles = {
  button: {
    padding: 15,
    backgroundColor: '#4CAF50',
    color: 'white',
    border: 'none',
    borderRadius: 8,
    fontSize: 16,
    fontWeight: 'bold',
    cursor: 'pointer'
  }
};