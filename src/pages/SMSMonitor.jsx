

// import { useEffect, useState } from 'react';
// import { Capacitor } from '@capacitor/core';
// import { useNavigate } from 'react-router-dom';
// import SMSReceiver from '../plugins/sms-receiver';
// import EmergencyAlert from '../plugins/emergency-alert';

// export default function SMSMonitor() {
//   const [phoneNumber, setPhoneNumber] = useState('+919899144393');
//   const [isListening, setIsListening] = useState(false);
//   const [hasPermission, setHasPermission] = useState(false);
//   const [monitoredNumber, setMonitoredNumber] = useState('');
//   const navigate = useNavigate();

//   useEffect(() => {
//     if (Capacitor.getPlatform() === 'android') {
//       checkPermissions();
//       loadMonitoredNumber();
      
//       // Check for emergency immediately on mount
//       checkForEmergency();
      
//       // Then check every 2 seconds
//       const interval = setInterval(checkForEmergency, 2000);
//       return () => clearInterval(interval);
//     }
//   }, []);

//   const checkForEmergency = async () => {
//     try {
//       const data = await EmergencyAlert.getEmergencyData();
//       if (data.hasEmergency) {
//         console.log('Emergency detected! Redirecting...');
//         navigate('/emergency-alert');
//       }
//     } catch (error) {
//       console.error('Error checking emergency:', error);
//     }
//   };

//   // ... rest of your existing code (keep all your existing functions)

//   const checkPermissions = async () => {
//     try {
//       const result = await SMSReceiver.checkPermissions();
//       const granted = result.sms === 'granted';
//       setHasPermission(granted);
//       return granted;
//     } catch (error) {
//       console.error('Error checking permissions:', error);
//       return false;
//     }
//   };

//   const requestPermissions = async () => {
//     try {
//       const result = await SMSReceiver.requestPermissions();
//       const granted = result.sms === 'granted';
//       setHasPermission(granted);
      
//       if (granted) {
//         alert('Permissions granted! You can now monitor SMS.');
//       } else {
//         alert('Permissions denied. Please enable SMS and notification permissions in settings.');
//       }
      
//       return granted;
//     } catch (error) {
//       console.error('Error requesting permissions:', error);
//       alert('Error: ' + error.message);
//       return false;
//     }
//   };

//   const loadMonitoredNumber = async () => {
//     try {
//       const result = await SMSReceiver.getMonitoredNumber();
//       setMonitoredNumber(result.phoneNumber);
//       if (result.phoneNumber) {
//         setIsListening(true);
//       }
//     } catch (error) {
//       console.error('Error loading monitored number:', error);
//     }
//   };

//   const startMonitoring = async () => {
//     if (!phoneNumber.trim()) {
//       alert('Please enter a phone number to monitor');
//       return;
//     }

//     try {
//       if (!hasPermission) {
//         const granted = await requestPermissions();
//         if (!granted) return;
//       }

//       const result = await SMSReceiver.startListening({
//         phoneNumber: phoneNumber.trim()
//       });

//       if (result.success) {
//         setIsListening(true);
//         setMonitoredNumber(phoneNumber.trim());
//         alert(`✅ Emergency monitoring started!\n\nMonitoring: ${phoneNumber}\n\nWhen SMS arrives:\n• Phone will vibrate continuously\n• Loud alarm will sound\n• Emergency alert will open\n• Location will be extracted`);
//       }
//     } catch (error) {
//       console.error('Error starting monitoring:', error);
//       alert('Error: ' + error.message);
//     }
//   };

//   const stopMonitoring = async () => {
//     try {
//       const result = await SMSReceiver.stopListening();
      
//       if (result.success) {
//         setIsListening(false);
//         alert('Stopped monitoring SMS');
//       }
//     } catch (error) {
//       console.error('Error stopping monitoring:', error);
//       alert('Error: ' + error.message);
//     }
//   };

//   if (Capacitor.getPlatform() !== 'android') {
//     return (
//       <div style={{ padding: 20 }}>
//         <h2>Emergency SMS Monitor</h2>
//         <p style={{ color: 'red' }}>
//           SMS monitoring only works on Android devices.
//         </p>
//       </div>
//     );
//   }

//   return (
//     <div style={{ padding: 20, fontFamily: 'Arial, sans-serif' }}>
//       <h2>🚨 Emergency SMS Monitor</h2>
      
//       <div style={{ 
//         padding: 15, 
//         backgroundColor: '#f0f0f0', 
//         borderRadius: 8,
//         marginBottom: 20 
//       }}>
//         <p style={{ margin: '5px 0' }}>
//           <strong>Permission Status:</strong>{' '}
//           <span style={{ color: hasPermission ? 'green' : 'red' }}>
//             {hasPermission ? '✓ Granted' : '✗ Not Granted'}
//           </span>
//         </p>
//         <p style={{ margin: '5px 0' }}>
//           <strong>Monitoring Status:</strong>{' '}
//           <span style={{ color: isListening ? 'green' : 'orange' }}>
//             {isListening ? '🟢 Active' : '🔴 Inactive'}
//           </span>
//         </p>
//         {monitoredNumber && (
//           <p style={{ margin: '5px 0' }}>
//             <strong>Monitored Number:</strong> {monitoredNumber}
//           </p>
//         )}
//       </div>

//       {!hasPermission && (
//         <div style={{ 
//           padding: 15, 
//           backgroundColor: '#fff3cd', 
//           borderRadius: 8,
//           marginBottom: 20,
//           border: '1px solid #ffc107'
//         }}>
//           <p style={{ margin: 0 }}>
//             ⚠️ Permissions required. Click "Request Permissions" below.
//           </p>
//         </div>
//       )}

//       <div style={{ marginBottom: 20 }}>
//         <label style={{ display: 'block', marginBottom: 10, fontWeight: 'bold' }}>
//           Emergency Number to Monitor:
//         </label>
//         <input
//           type="tel"
//           value={phoneNumber}
//           onChange={(e) => setPhoneNumber(e.target.value)}
//           placeholder="+919899144393"
//           style={{
//             width: '100%',
//             padding: 12,
//             fontSize: 16,
//             borderRadius: 5,
//             border: '1px solid #ccc',
//             boxSizing: 'border-box'
//           }}
//         />
//         <p style={{ fontSize: 12, color: '#666', margin: '5px 0' }}>
//           Default: +919899144393 (Emergency contact)
//         </p>
//       </div>

//       <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
//         {!hasPermission && (
//           <button
//             onClick={requestPermissions}
//             style={{
//               padding: 15,
//               backgroundColor: '#2196F3',
//               color: 'white',
//               border: 'none',
//               borderRadius: 5,
//               cursor: 'pointer',
//               fontSize: 16,
//               fontWeight: 'bold'
//             }}
//           >
//             🔐 Request Permissions
//           </button>
//         )}

//         {!isListening ? (
//           <button
//             onClick={startMonitoring}
//             disabled={!hasPermission}
//             style={{
//               padding: 15,
//               backgroundColor: hasPermission ? '#4CAF50' : '#ccc',
//               color: 'white',
//               border: 'none',
//               borderRadius: 5,
//               cursor: hasPermission ? 'pointer' : 'not-allowed',
//               fontSize: 16,
//               fontWeight: 'bold'
//             }}
//           >
//             ▶️ Start Emergency Monitoring
//           </button>
//         ) : (
//           <button
//             onClick={stopMonitoring}
//             style={{
//               padding: 15,
//               backgroundColor: '#f44336',
//               color: 'white',
//               border: 'none',
//               borderRadius: 5,
//               cursor: 'pointer',
//               fontSize: 16,
//               fontWeight: 'bold'
//             }}
//           >
//             ⏹️ Stop Monitoring
//           </button>
//         )}

//         <button
//           onClick={() => navigate('/emergency-alert')}
//           style={{
//             padding: 15,
//             backgroundColor: '#9C27B0',
//             color: 'white',
//             border: 'none',
//             borderRadius: 5,
//             cursor: 'pointer',
//             fontSize: 16
//           }}
//         >
//           📋 View Emergency Alerts
//         </button>
//       </div>

//       <div style={{ 
//         marginTop: 30,
//         padding: 15,
//         backgroundColor: '#e3f2fd',
//         borderRadius: 8,
//         border: '1px solid #2196F3'
//       }}>
//         <h3 style={{ marginTop: 0 }}>ℹ️ How Emergency Monitoring Works:</h3>
//         <ul style={{ lineHeight: 1.8 }}>
//           <li>Enter the emergency number (+919899144393)</li>
//           <li>Click "Start Emergency Monitoring"</li>
//           <li>When emergency SMS arrives:
//             <ul>
//               <li>📳 Phone vibrates continuously</li>
//               <li>🔊 Loud alarm sounds continuously</li>
//               <li>🚨 App opens to emergency alert page</li>
//               <li>📍 GPS coordinates are extracted</li>
//               <li>🗺️ Map navigation available</li>
//               <li>📞 Quick call button</li>
//             </ul>
//           </li>
//           <li><strong>Works even when app is closed!</strong></li>
//         </ul>
//       </div>
//     </div>
//   );
// }


import { useEffect, useState } from 'react';
import { Capacitor } from '@capacitor/core';
import { useNavigate } from 'react-router-dom';
import SMSReceiver from '../plugins/sms-receiver';
import EmergencyAlert from '../plugins/emergency-alert';
import Permissions from '../plugins/permissions';

export default function SMSMonitor() {
  const [phoneNumber, setPhoneNumber] = useState('+919899144393');
  const [isListening, setIsListening] = useState(false);
  const [monitoredNumber, setMonitoredNumber] = useState('');
  const [permissions, setPermissions] = useState({
    sms: false,
    notifications: false,
    overlay: false,
    vibrate: false,
    wakelock: false,
    audio: false
  });
  const navigate = useNavigate();

  useEffect(() => {
    if (Capacitor.getPlatform() === 'android') {
      checkAllPermissions();
      loadMonitoredNumber();
      
      // Check for emergency
      checkForEmergency();
      const interval = setInterval(checkForEmergency, 2000);
      return () => clearInterval(interval);
    }
  }, []);

  const checkAllPermissions = async () => {
    try {
      const result = await Permissions.checkAllPermissions();
      setPermissions(result);
      console.log('Permissions:', result);
    } catch (error) {
      console.error('Error checking permissions:', error);
    }
  };

  const requestAllPermissions = async () => {
    try {
      await Permissions.requestAllPermissions();
      // Wait a bit then check again
      setTimeout(checkAllPermissions, 1000);
    } catch (error) {
      console.error('Error requesting permissions:', error);
      alert('Error: ' + error.message);
    }
  };

  const requestOverlayPermission = async () => {
    try {
      const result = await Permissions.requestOverlayPermission();
      if (result.success) {
        alert('Please enable "Display over other apps" permission and come back to the app');
        // Check permission after user returns
        setTimeout(checkAllPermissions, 3000);
      } else if (result.granted) {
        alert('Overlay permission already granted!');
        checkAllPermissions();
      }
    } catch (error) {
      console.error('Error requesting overlay:', error);
    }
  };

  const openAppSettings = async () => {
    try {
      await Permissions.openAppSettings();
      alert('Please enable all permissions in Settings and come back');
    } catch (error) {
      console.error('Error opening settings:', error);
    }
  };

  const checkForEmergency = async () => {
    try {
      const data = await EmergencyAlert.getEmergencyData();
      if (data.hasEmergency) {
        navigate('/emergency-alert');
      }
    } catch (error) {
      console.error('Error checking emergency:', error);
    }
  };

  const loadMonitoredNumber = async () => {
    try {
      const result = await SMSReceiver.getMonitoredNumber();
      setMonitoredNumber(result.phoneNumber);
      if (result.phoneNumber) {
        setIsListening(true);
      }
    } catch (error) {
      console.error('Error loading monitored number:', error);
    }
  };

  const startMonitoring = async () => {
    if (!phoneNumber.trim()) {
      alert('Please enter a phone number to monitor');
      return;
    }

    // Check if all required permissions are granted
    if (!permissions.sms || !permissions.notifications || !permissions.vibrate) {
      alert('Please grant all required permissions first!');
      return;
    }

    if (!permissions.overlay) {
      alert('Overlay permission is recommended for auto-opening the app when screen is locked. Please enable it.');
    }

    try {
      const result = await SMSReceiver.startListening({
        phoneNumber: phoneNumber.trim()
      });

      if (result.success) {
        setIsListening(true);
        setMonitoredNumber(phoneNumber.trim());
        alert(`✅ Emergency monitoring started!\n\nMonitoring: ${phoneNumber}\n\nWhen SMS arrives:\n• Phone will vibrate continuously\n• Loud alarm will sound\n• Emergency alert will open\n• Location will be extracted`);
      }
    } catch (error) {
      console.error('Error starting monitoring:', error);
      alert('Error: ' + error.message);
    }
  };

  const stopMonitoring = async () => {
    try {
      const result = await SMSReceiver.stopListening();
      
      if (result.success) {
        setIsListening(false);
        alert('Stopped monitoring SMS');
      }
    } catch (error) {
      console.error('Error stopping monitoring:', error);
      alert('Error: ' + error.message);
    }
  };

  const allPermissionsGranted = permissions.sms && 
                                 permissions.notifications && 
                                 permissions.overlay && 
                                 permissions.vibrate;

  if (Capacitor.getPlatform() !== 'android') {
    return (
      <div style={{ padding: 20 }}>
        <h2>Emergency SMS Monitor</h2>
        <p style={{ color: 'red' }}>
          SMS monitoring only works on Android devices.
        </p>
      </div>
    );
  }

  return (
    <div style={{ padding: 20, fontFamily: 'Arial, sans-serif', maxWidth: 600, margin: '0 auto' }}>
      <h2 style={{ textAlign: 'center' }}>🚨 Emergency SMS Monitor</h2>
      
      {/* Permissions Status Card */}
      <div style={{ 
        padding: 20, 
        backgroundColor: allPermissionsGranted ? '#e8f5e9' : '#fff3e0', 
        borderRadius: 12,
        marginBottom: 20,
        border: `2px solid ${allPermissionsGranted ? '#4CAF50' : '#FF9800'}`
      }}>
        <h3 style={{ marginTop: 0, marginBottom: 15 }}>📋 Permissions Status</h3>
        
        <div style={{ marginBottom: 10 }}>
          <PermissionItem 
            label="SMS (Read & Receive)" 
            granted={permissions.sms} 
            required={true}
          />
          <PermissionItem 
            label="Notifications" 
            granted={permissions.notifications} 
            required={true}
          />
          <PermissionItem 
            label="Vibration" 
            granted={permissions.vibrate} 
            required={true}
          />
          <PermissionItem 
            label="Display Over Other Apps" 
            granted={permissions.overlay} 
            required={false}
            note="(Recommended for auto-opening when locked)"
          />
          <PermissionItem 
            label="Wake Lock" 
            granted={permissions.wakelock} 
            required={false}
          />
          <PermissionItem 
            label="Audio Modification" 
            granted={permissions.audio} 
            required={false}
          />
        </div>

        {!allPermissionsGranted && (
          <div style={{
            backgroundColor: '#fff3cd',
            padding: 15,
            borderRadius: 8,
            marginTop: 15,
            border: '1px solid #ffc107'
          }}>
            <p style={{ margin: 0, fontWeight: 'bold', color: '#856404' }}>
              ⚠️ Some required permissions are missing!
            </p>
          </div>
        )}
      </div>

      {/* Permission Buttons */}
      <div style={{ marginBottom: 20, display: 'flex', flexDirection: 'column', gap: 10 }}>
        <button
          onClick={requestAllPermissions}
          style={{
            padding: 15,
            backgroundColor: '#2196F3',
            color: 'white',
            border: 'none',
            borderRadius: 8,
            cursor: 'pointer',
            fontSize: 16,
            fontWeight: 'bold',
            boxShadow: '0 2px 4px rgba(0,0,0,0.2)'
          }}
        >
          🔐 Request All Permissions
        </button>

        {!permissions.overlay && (
          <button
            onClick={requestOverlayPermission}
            style={{
              padding: 15,
              backgroundColor: '#FF9800',
              color: 'white',
              border: 'none',
              borderRadius: 8,
              cursor: 'pointer',
              fontSize: 16,
              fontWeight: 'bold',
              boxShadow: '0 2px 4px rgba(0,0,0,0.2)'
            }}
          >
            📱 Enable Overlay Permission
          </button>
        )}

        <button
          onClick={openAppSettings}
          style={{
            padding: 12,
            backgroundColor: '#607D8B',
            color: 'white',
            border: 'none',
            borderRadius: 8,
            cursor: 'pointer',
            fontSize: 14
          }}
        >
          ⚙️ Open App Settings
        </button>
      </div>

      {/* Monitoring Status */}
      <div style={{ 
        padding: 15, 
        backgroundColor: '#f0f0f0', 
        borderRadius: 8,
        marginBottom: 20 
      }}>
        <p style={{ margin: '5px 0' }}>
          <strong>Monitoring Status:</strong>{' '}
          <span style={{ 
            color: isListening ? 'green' : 'orange',
            fontWeight: 'bold'
          }}>
            {isListening ? '🟢 ACTIVE' : '🔴 INACTIVE'}
          </span>
        </p>
        {monitoredNumber && (
          <p style={{ margin: '5px 0' }}>
            <strong>Monitored Number:</strong> {monitoredNumber}
          </p>
        )}
      </div>

      {/* Phone Number Input */}
      <div style={{ marginBottom: 20 }}>
        <label style={{ display: 'block', marginBottom: 10, fontWeight: 'bold' }}>
          Emergency Number to Monitor:
        </label>
        <input
          type="tel"
          value={phoneNumber}
          onChange={(e) => setPhoneNumber(e.target.value)}
          placeholder="+919899144393"
          style={{
            width: '100%',
            padding: 12,
            fontSize: 16,
            borderRadius: 8,
            border: '2px solid #ccc',
            boxSizing: 'border-box'
          }}
        />
        <p style={{ fontSize: 12, color: '#666', margin: '5px 0' }}>
          Default: +919899144393 (Emergency contact)
        </p>
      </div>

      {/* Action Buttons */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
        {!isListening ? (
          <button
            onClick={startMonitoring}
            disabled={!permissions.sms || !permissions.notifications}
            style={{
              padding: 18,
              backgroundColor: (permissions.sms && permissions.notifications) ? '#4CAF50' : '#ccc',
              color: 'white',
              border: 'none',
              borderRadius: 8,
              cursor: (permissions.sms && permissions.notifications) ? 'pointer' : 'not-allowed',
              fontSize: 18,
              fontWeight: 'bold',
              boxShadow: '0 3px 6px rgba(0,0,0,0.3)'
            }}
          >
            ▶️ START EMERGENCY MONITORING
          </button>
        ) : (
          <button
            onClick={stopMonitoring}
            style={{
              padding: 18,
              backgroundColor: '#f44336',
              color: 'white',
              border: 'none',
              borderRadius: 8,
              cursor: 'pointer',
              fontSize: 18,
              fontWeight: 'bold',
              boxShadow: '0 3px 6px rgba(0,0,0,0.3)'
            }}
          >
            ⏹️ STOP MONITORING
          </button>
        )}

        <button
          onClick={() => navigate('/emergency-alert')}
          style={{
            padding: 15,
            backgroundColor: '#9C27B0',
            color: 'white',
            border: 'none',
            borderRadius: 8,
            cursor: 'pointer',
            fontSize: 16
          }}
        >
          📋 View Emergency Alerts
        </button>
      </div>

      {/* Info Section */}
      <div style={{ 
        marginTop: 30,
        padding: 20,
        backgroundColor: '#e3f2fd',
        borderRadius: 12,
        border: '2px solid #2196F3'
      }}>
        <h3 style={{ marginTop: 0 }}>ℹ️ How It Works:</h3>
        <ul style={{ lineHeight: 2, paddingLeft: 20 }}>
          <li>Grant all required permissions</li>
          <li>Enter emergency contact number</li>
          <li>Click "Start Emergency Monitoring"</li>
          <li><strong>When emergency SMS arrives:</strong>
            <ul style={{ marginTop: 10 }}>
              <li>📳 Continuous vibration</li>
              <li>🔊 Loud alarm sound (looping)</li>
              <li>🚨 App auto-opens to alert page</li>
              <li>📍 GPS coordinates extracted</li>
              <li>🗺️ Navigate to location</li>
              <li>📞 Quick call button</li>
            </ul>
          </li>
          <li><strong style={{ color: '#f44336' }}>Works even when app is closed or phone is locked!</strong></li>
        </ul>

        <div style={{ 
          marginTop: 15,
          padding: 15,
          backgroundColor: 'white',
          borderRadius: 8,
          border: '1px solid #2196F3'
        }}>
          <h4 style={{ marginTop: 0 }}>📨 SMS Format Examples:</h4>
          <code style={{ 
            display: 'block', 
            fontSize: 13,
            lineHeight: 1.8
          }}>
            Emergency! Lat: 28.7041, Lon: 77.1025<br/>
            Help! Location: 28.7041,77.1025<br/>
            SOS 28.7041, 77.1025
          </code>
        </div>
      </div>
    </div>
  );
}

// Helper component for permission items
function PermissionItem({ label, granted, required, note }) {
  return (
    <div style={{ 
      display: 'flex', 
      alignItems: 'center', 
      marginBottom: 8,
      padding: 10,
      backgroundColor: granted ? '#e8f5e9' : '#ffebee',
      borderRadius: 6
    }}>
      <span style={{ 
        fontSize: 20, 
        marginRight: 10 
      }}>
        {granted ? '✅' : '❌'}
      </span>
      <div style={{ flex: 1 }}>
        <strong>{label}</strong>
        {required && <span style={{ color: '#f44336', marginLeft: 5 }}>*</span>}
        {note && <div style={{ fontSize: 11, color: '#666' }}>{note}</div>}
      </div>
    </div>
  );
}

