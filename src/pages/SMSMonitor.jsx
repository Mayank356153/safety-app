// import AppPreferences from '../plugins/app-preferences';
// import { useEffect, useState } from 'react';
// import { Capacitor } from '@capacitor/core';
// import { useNavigate } from 'react-router-dom';
// import SMSReceiver from '../plugins/sms-receiver';
// import EmergencyAlert from '../plugins/emergency-alert';
// import Permissions from '../plugins/permissions';
// import LocationTracker from '../plugins/location-tracker';


// export default function SMSMonitor() {

//   const [phoneNumber, setPhoneNumber] = useState('+919899144393');
//   const [isListening, setIsListening] = useState(false);
//   const [monitoredNumber, setMonitoredNumber] = useState('');
//   const [permissions, setPermissions] = useState({
//     sms: false,
//     notifications: false,
//     overlay: false,
//     vibrate: false,
//     wakelock: false,
//     audio: false
//   });
//   const [locationTracking, setLocationTracking] = useState(false);

//   const navigate = useNavigate();

//   useEffect(() => {
//   if (Capacitor.getPlatform() === 'android') {
//     checkAllPermissions();
//     loadMonitoredNumber();
//     checkForEmergency();
    
//     // Auto-start location tracking
//     startLocationTracking();
    
//     const interval = setInterval(checkForEmergency, 2000);
//     return () => clearInterval(interval);
//   }
// }, []);



// const startLocationTracking = async () => {
//   try {
//     const permCheck = await LocationTracker.checkLocationPermission();
//     if (!permCheck.granted) {
//       const permResult = await LocationTracker.requestLocationPermission();
//       if (!permResult.granted) {
//         alert('Location permission required');
//         return;
//       }
//     }

//     const userId = localStorage.getItem('userId');
//     const serverUrl = localStorage.getItem('serverUrl');

//     console.log('Starting location tracking...');
//     console.log('User ID:', userId);
//     console.log('Server URL:', serverUrl);

//     // Save to Android SharedPreferences
//     if (Capacitor.getPlatform() === 'android') {
//       await AppPreferences.saveLocationSettings({
//         userId: userId,
//         serverUrl: serverUrl
//       });
//       console.log('✅ Settings saved to native storage');
//     }

//     // Start the service
//     const result = await LocationTracker.startLocationUpdates();
//     console.log('Service started:', result);

//     setLocationTracking(true);
//     alert('✅ Location tracking started!\n\nYour location will update every 2 minutes.');
//   } catch (error) {
//     console.error('Error starting location tracking:', error);
//     alert('Error: ' + error.message);
//   }
// };

//   const checkAllPermissions = async () => {
//     try {
//       const result = await Permissions.checkAllPermissions();
//       setPermissions(result);
//       console.log('Permissions:', result);
//     } catch (error) {
//       console.error('Error checking permissions:', error);
//     }
//   };

//   const requestAllPermissions = async () => {
//     try {
//       await Permissions.requestAllPermissions();
//       // Wait a bit then check again
//       setTimeout(checkAllPermissions, 1000);
//     } catch (error) {
//       console.error('Error requesting permissions:', error);
//       alert('Error: ' + error.message);
//     }
//   };


//   const requestOverlayPermission = async () => {
//     try {
//       const result = await Permissions.requestOverlayPermission();
//       if (result.success) {
//         alert('Please enable "Display over other apps" permission and come back to the app');
//         // Check permission after user returns
//         setTimeout(checkAllPermissions, 3000);
//       } else if (result.granted) {
//         alert('Overlay permission already granted!');
//         checkAllPermissions();
//       }
//     } catch (error) {
//       console.error('Error requesting overlay:', error);
//     }
//   };

//   const openAppSettings = async () => {
//     try {
//       await Permissions.openAppSettings();
//       alert('Please enable all permissions in Settings and come back');
//     } catch (error) {
//       console.error('Error opening settings:', error);
//     }
//   };

//   const checkForEmergency = async () => {
//     try {
//       const data = await EmergencyAlert.getEmergencyData();
//       if (data.hasEmergency) {
//         navigate('/emergency-alert');
//       }
//     } catch (error) {
//       console.error('Error checking emergency:', error);
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

//     // Check if all required permissions are granted
//     if (!permissions.sms || !permissions.notifications || !permissions.vibrate) {
//       alert('Please grant all required permissions first!');
//       return;
//     }

//     if (!permissions.overlay) {
//       alert('Overlay permission is recommended for auto-opening the app when screen is locked. Please enable it.');
//     }

//     try {
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

//   const allPermissionsGranted = permissions.sms && 
//                                  permissions.notifications && 
//                                  permissions.overlay && 
//                                  permissions.vibrate;

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
//     <div style={{ padding: 20, fontFamily: 'Arial, sans-serif', maxWidth: 600, margin: '0 auto' }}>
//       <h2 style={{ textAlign: 'center' }}>🚨 Emergency SMS Monitor</h2>
      
//       {/* Permissions Status Card */}
//       <div style={{ 
//         padding: 20, 
//         backgroundColor: allPermissionsGranted ? '#e8f5e9' : '#fff3e0', 
//         borderRadius: 12,
//         marginBottom: 20,
//         border: `2px solid ${allPermissionsGranted ? '#4CAF50' : '#FF9800'}`
//       }}>
//         <h3 style={{ marginTop: 0, marginBottom: 15 }}>📋 Permissions Status</h3>
        
//         <div style={{ marginBottom: 10 }}>
//           <PermissionItem 
//             label="SMS (Read & Receive)" 
//             granted={permissions.sms} 
//             required={true}
//           />
//           <PermissionItem 
//             label="Notifications" 
//             granted={permissions.notifications} 
//             required={true}
//           />
//           <PermissionItem 
//             label="Vibration" 
//             granted={permissions.vibrate} 
//             required={true}
//           />
//           <PermissionItem 
//             label="Display Over Other Apps" 
//             granted={permissions.overlay} 
//             required={false}
//             note="(Recommended for auto-opening when locked)"
//           />
//           <PermissionItem 
//             label="Wake Lock" 
//             granted={permissions.wakelock} 
//             required={false}
//           />
//           <PermissionItem 
//             label="Audio Modification" 
//             granted={permissions.audio} 
//             required={false}
//           />
//         </div>

//         {!allPermissionsGranted && (
//           <div style={{
//             backgroundColor: '#fff3cd',
//             padding: 15,
//             borderRadius: 8,
//             marginTop: 15,
//             border: '1px solid #ffc107'
//           }}>
//             <p style={{ margin: 0, fontWeight: 'bold', color: '#856404' }}>
//               ⚠️ Some required permissions are missing!
//             </p>
//           </div>
//         )}
//       </div>

//       {/* Permission Buttons */}
//       <div style={{ marginBottom: 20, display: 'flex', flexDirection: 'column', gap: 10 }}>
//         <button
//           onClick={requestAllPermissions}
//           style={{
//             padding: 15,
//             backgroundColor: '#2196F3',
//             color: 'white',
//             border: 'none',
//             borderRadius: 8,
//             cursor: 'pointer',
//             fontSize: 16,
//             fontWeight: 'bold',
//             boxShadow: '0 2px 4px rgba(0,0,0,0.2)'
//           }}
//         >
//           🔐 Request All Permissions
//         </button>

//         {!permissions.overlay && (
//           <button
//             onClick={requestOverlayPermission}
//             style={{
//               padding: 15,
//               backgroundColor: '#FF9800',
//               color: 'white',
//               border: 'none',
//               borderRadius: 8,
//               cursor: 'pointer',
//               fontSize: 16,
//               fontWeight: 'bold',
//               boxShadow: '0 2px 4px rgba(0,0,0,0.2)'
//             }}
//           >
//             📱 Enable Overlay Permission
//           </button>
//         )}

//         <button
//           onClick={openAppSettings}
//           style={{
//             padding: 12,
//             backgroundColor: '#607D8B',
//             color: 'white',
//             border: 'none',
//             borderRadius: 8,
//             cursor: 'pointer',
//             fontSize: 14
//           }}
//         >
//           ⚙️ Open App Settings
//         </button>
//       </div>

//       {/* Monitoring Status */}
//       <div style={{ 
//         padding: 15, 
//         backgroundColor: '#f0f0f0', 
//         borderRadius: 8,
//         marginBottom: 20 
//       }}>
//         <p style={{ margin: '5px 0' }}>
//           <strong>Monitoring Status:</strong>{' '}
//           <span style={{ 
//             color: isListening ? 'green' : 'orange',
//             fontWeight: 'bold'
//           }}>
//             {isListening ? '🟢 ACTIVE' : '🔴 INACTIVE'}
//           </span>
//         </p>
//         {monitoredNumber && (
//           <p style={{ margin: '5px 0' }}>
//             <strong>Monitored Number:</strong> {monitoredNumber}
//           </p>
//         )}
//       </div>

//       {/* Phone Number Input */}
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
//             borderRadius: 8,
//             border: '2px solid #ccc',
//             boxSizing: 'border-box'
//           }}
//         />
//         <p style={{ fontSize: 12, color: '#666', margin: '5px 0' }}>
//           Default: +919899144393 (Emergency contact)
//         </p>
//       </div>
//       {/* Action Buttons */}
//       <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
//         {!isListening ? (
//           <button
//             onClick={startMonitoring}
//             disabled={!permissions.sms || !permissions.notifications}
//             style={{
//               padding: 18,
//               backgroundColor: (permissions.sms && permissions.notifications) ? '#4CAF50' : '#ccc',
//               color: 'white',
//               border: 'none',
//               borderRadius: 8,
//               cursor: (permissions.sms && permissions.notifications) ? 'pointer' : 'not-allowed',
//               fontSize: 18,
//               fontWeight: 'bold',
//               boxShadow: '0 3px 6px rgba(0,0,0,0.3)'
//             }}
//           >
//             ▶️ START EMERGENCY MONITORING
//           </button>
//         ) : (
//           <button 
//             onClick={stopMonitoring}
//             style={{
//               padding: 18,
//               backgroundColor: '#f44336',
//               color: 'white',
//               border: 'none',
//               borderRadius: 8,
//               cursor: 'pointer',
//               fontSize: 18,
//               fontWeight: 'bold',
//               boxShadow: '0 3px 6px rgba(0,0,0,0.3)'
//             }}
//           >
//             ⏹️ STOP MONITORING
//           </button>
//         )}

//          <button onClick={()=>navigate("/debug")}
//             // onClick={stopMonitoring}
//             style={{
//               padding: 18,
//               backgroundColor: '#f44336',
//               color: 'white',
//               border: 'none',
//               borderRadius: 8,
//               cursor: 'pointer',
//               fontSize: 18,
//               fontWeight: 'bold',
//               boxShadow: '0 3px 6px rgba(0,0,0,0.3)'
//             }}
//           >
//             Debug Info
//           </button>
//          <button onClick={()=>navigate("/service-status")}
//             // onClick={stopMonitoring}
//             style={{
//               padding: 18,
//               backgroundColor: '#f44336',
//               color: 'white',
//               border: 'none',
//               borderRadius: 8,
//               cursor: 'pointer',
//               fontSize: 18,
//               fontWeight: 'bold',
//               boxShadow: '0 3px 6px rgba(0,0,0,0.3)'
//             }}
//           >
//             Service Status
//           </button>

//         <button
//           onClick={() => navigate('/emergency-alert')}
//           style={{
//             padding: 15,
//             backgroundColor: '#9C27B0',
//             color: 'white',
//             border: 'none',
//             borderRadius: 8,
//             cursor: 'pointer',
//             fontSize: 16
//           }}
//         >
//           📋 View Emergency Alerts
//         </button>
//       </div>

//       {/* Info Section */}
//       <div style={{ 
//         marginTop: 30,
//         padding: 20,
//         backgroundColor: '#e3f2fd',
//         borderRadius: 12,
//         border: '2px solid #2196F3'
//       }}>
//         <h3 style={{ marginTop: 0 }}>ℹ️ How It Works:</h3>
//         <ul style={{ lineHeight: 2, paddingLeft: 20 }}>
//           <li>Grant all required permissions</li>
//           <li>Enter emergency contact number</li>
//           <li>Click "Start Emergency Monitoring"</li>
//           <li><strong>When emergency SMS arrives:</strong>
//             <ul style={{ marginTop: 10 }}>
//               <li>📳 Continuous vibration</li>
//               <li>🔊 Loud alarm sound (looping)</li>
//               <li>🚨 App auto-opens to alert page</li>
//               <li>📍 GPS coordinates extracted</li>
//               <li>🗺️ Navigate to location</li>
//               <li>📞 Quick call button</li>
//             </ul>
//           </li>
//           <li><strong style={{ color: '#f44336' }}>Works even when app is closed or phone is locked!</strong></li>
//         </ul>

//         <div style={{ 
//           marginTop: 15,
//           padding: 15,
//           backgroundColor: 'white',
//           borderRadius: 8,
//           border: '1px solid #2196F3'
//         }}>
//           <h4 style={{ marginTop: 0 }}>📨 SMS Format Examples:</h4>
//           <code style={{ 
//             display: 'block', 
//             fontSize: 13,
//             lineHeight: 1.8
//           }}>
//             Emergency! Lat: 28.7041, Lon: 77.1025<br/>
//             Help! Location: 28.7041,77.1025<br/>
//             SOS 28.7041, 77.1025
//           </code>
//         </div>
//       </div>
//     </div>
//   );
// }

// // Helper component for permission items
// function PermissionItem({ label, granted, required, note }) {
//   return (
//     <div style={{ 
//       display: 'flex', 
//       alignItems: 'center', 
//       marginBottom: 8,
//       padding: 10,
//       backgroundColor: granted ? '#e8f5e9' : '#ffebee',
//       borderRadius: 6
//     }}>
//       <span style={{ 
//         fontSize: 20, 
//         marginRight: 10 
//       }}>
//         {granted ? '✅' : '❌'}
//       </span>
//       <div style={{ flex: 1 }}>
//         <strong>{label}</strong>
//         {required && <span style={{ color: '#f44336', marginLeft: 5 }}>*</span>}
//         {note && <div style={{ fontSize: 11, color: '#666' }}>{note}</div>}
//       </div>
//     </div>
//   );
// }

import AppPreferences from '../plugins/app-preferences';
import { useEffect, useState, useCallback } from 'react';
import { Capacitor } from '@capacitor/core';
import { useNavigate } from 'react-router-dom';
import SMSReceiver from '../plugins/sms-receiver';
import EmergencyAlert from '../plugins/emergency-alert';
import Permissions from '../plugins/permissions';
import LocationTracker from '../plugins/location-tracker';

// Icons (Simulated with emoji for zero-dependency, but ready for Lucide/FontAwesome)
const Icons = {
  Sms: "💬",
  Bell: "🔔",
  Vibrate: "📳",
  Overlay: "🖥️",
  Audio: "🔊",
  Settings: "⚙️",
  Location: "📍",
  Shield: "🛡️"
};

export default function SMSMonitor() {
  const [phoneNumber, setPhoneNumber] = useState('+91');
  const [isListening, setIsListening] = useState(false);
  const [monitoredNumber, setMonitoredNumber] = useState('');
  const [permissions, setPermissions] = useState({
    sms: false, notifications: false, overlay: false, vibrate: false, wakelock: false, audio: false
  });
  const [locationTracking, setLocationTracking] = useState(false);

  const navigate = useNavigate();

  // Functional Logic Preserved
  useEffect(() => {
    if (Capacitor.getPlatform() === 'android') {
      checkAllPermissions();
      loadMonitoredNumber();
      checkForEmergency();
      startLocationTracking();
      const interval = setInterval(checkForEmergency, 2000);
      return () => clearInterval(interval);
    }
  }, []);

  // --- All original functions remain identical in logic ---
 
const startLocationTracking = async () => {
  try {
    const permCheck = await LocationTracker.checkLocationPermission();
    if (!permCheck.granted) {
      const permResult = await LocationTracker.requestLocationPermission();
      if (!permResult.granted) {
        alert('Location permission required');
        return;
      }
    }

    const userId = localStorage.getItem('userId');
    const serverUrl = localStorage.getItem('serverUrl');

    console.log('Starting location tracking...');
    console.log('User ID:', userId);
    console.log('Server URL:', serverUrl);

    // Save to Android SharedPreferences
    if (Capacitor.getPlatform() === 'android') {
      await AppPreferences.saveLocationSettings({
        userId: userId,
        serverUrl: serverUrl
      });
      console.log('✅ Settings saved to native storage');
    }

    // Start the service
    const result = await LocationTracker.startLocationUpdates();
    console.log('Service started:', result);

    setLocationTracking(true);
    // alert('✅ Location tracking started!\n\nYour location will update every 2 minutes.');
  } catch (error) {
    console.error('Error starting location tracking:', error);
    alert('Error: ' + error.message);
  }
};

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
        // alert('Overlay permission already granted!');
        checkAllPermissions();
      }
    } catch (error) {
      console.error('Error requesting overlay:', error);
    }
  };

  const openAppSettings = async () => {
    try {
      await Permissions.openAppSettings();
      // alert('Please enable all permissions in Settings and come back');
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
        // alert(`✅ Emergency monitoring started!\n\nMonitoring: ${phoneNumber}\n\nWhen SMS arrives:\n• Phone will vibrate continuously\n• Loud alarm will sound\n• Emergency alert will open\n• Location will be extracted`);
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
        // alert('Stopped monitoring SMS');
      }
    } catch (error) {
      console.error('Error stopping monitoring:', error);
      alert('Error: ' + error.message);
    }
  };

  const allPermissionsGranted = permissions.sms && permissions.notifications && permissions.overlay && permissions.vibrate;

  if (Capacitor.getPlatform() !== 'android') {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen p-6 bg-gray-50 text-center">
        <div className="text-6xl mb-4">🤖</div>
        <h2 className="text-2xl font-bold text-gray-800">Android Only Feature</h2>
        <p className="text-gray-600 mt-2">SMS monitoring and background emergency services are exclusive to Android devices.</p>
        <button onClick={() => navigate(-1)} className="mt-6 px-6 py-2 bg-blue-600 text-white rounded-lg">Go Back</button>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 pb-10 font-sans">
      {/* Header Section */}
      <div className="bg-red-600 text-white p-6 shadow-lg rounded-b-3xl">
        <div className="max-w-md mx-auto flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-black tracking-tight">Guardian SMS</h1>
            <p className="text-red-100 text-sm opacity-90">Live Emergency Monitor</p>
          </div>
          <div className={`h-12 w-12 rounded-full flex items-center justify-center border-2 border-white/30 ${isListening ? 'bg-green-500 animate-pulse' : 'bg-red-700'}`}>
            {isListening ? '🛰️' : '💤'}
          </div>
        </div>
      </div>

      <div className="max-w-md mx-auto px-4 -mt-6">
        {/* Status Dashboard */}
        <div className="bg-white rounded-2xl shadow-xl p-5 mb-6">
          <div className="flex justify-between items-center mb-4">
            <h3 className="text-gray-500 text-xs font-bold uppercase tracking-widest">System Status</h3>
            <span className={`px-3 py-1 rounded-full text-[10px] font-bold ${isListening ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'}`}>
              {isListening ? 'MONITORING ACTIVE' : 'SYSTEM STANDBY'}
            </span>
          </div>
          
          <div className="grid grid-cols-2 gap-4">
            <div className="bg-gray-50 p-3 rounded-xl border border-gray-100">
              <p className="text-[10px] text-gray-400 font-bold mb-1">MONITORED CONTACT</p>
              <p className="text-sm font-mono font-bold text-gray-800 truncate">{monitoredNumber || 'None Set'}</p>
            </div>
            <div className="bg-gray-50 p-3 rounded-xl border border-gray-100">
              <p className="text-[10px] text-gray-400 font-bold mb-1">LOCATION SERVICE</p>
              <p className="text-sm font-bold text-gray-800">{locationTracking ? '✅ Syncing' : '❌ Disabled'}</p>
            </div>
          </div>
        </div>

        {/* Input Section */}
        <div className="bg-white rounded-2xl shadow-sm p-5 border border-gray-100 mb-6">
          <label className="block text-sm font-bold text-gray-700 mb-2">Primary Emergency Contact</label>
          <div className="relative">
            <span className="absolute left-4 top-3.5 text-gray-400">📞</span>
            <input
              type="tel"
              value={phoneNumber}
              onChange={(e) => setPhoneNumber(e.target.value)}
              className="w-full pl-10 pr-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:ring-2 focus:ring-red-500 focus:border-transparent outline-none transition-all"
              placeholder="+91..."
            />
          </div>
          <p className="text-[10px] text-gray-400 mt-2 italic">SMS from this number will trigger the high-priority alarm.</p>
        </div>

        {/* Permissions Grid */}
        <div className="bg-white rounded-2xl shadow-sm p-5 border border-gray-100 mb-6">
          <div className="flex justify-between items-center mb-4 border-b pb-2">
            <h3 className="font-bold text-gray-800 flex items-center gap-2">
              <span>{Icons.Shield}</span> Security Permissions
            </h3>
            <button onClick={openAppSettings} className="text-blue-600 text-xs font-bold">SETTINGS</button>
          </div>
          
          <div className="space-y-3">
            <PermissionTile icon={Icons.Sms} label="SMS Access" granted={permissions.sms} required />
            <PermissionTile icon={Icons.Bell} label="Notifications" granted={permissions.notifications} required />
            <PermissionTile icon={Icons.Vibrate} label="Vibration" granted={permissions.vibrate} required />
            <PermissionTile icon={Icons.Overlay} label="Screen Overlay" granted={permissions.overlay} />
          </div>

          {!allPermissionsGranted && (
            <button 
              onClick={requestAllPermissions}
              className="w-full mt-4 py-3 bg-blue-50 text-blue-600 rounded-xl text-sm font-bold border border-blue-100 hover:bg-blue-100 transition-colors"
            >
              Grant Missing Permissions
            </button>
          )}
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
        </div>

        {/* Main Action Button */}
        <div className="fixed bottom-6 left-0 right-0 px-6 max-w-md mx-auto">
          {!isListening ? (
            <button
              onClick={startMonitoring}
              disabled={!permissions.sms || !permissions.notifications}
              className="w-full bg-red-600 text-white py-4 rounded-2xl font-black text-lg shadow-2xl shadow-red-200 active:scale-95 transition-transform disabled:bg-gray-300 disabled:shadow-none uppercase tracking-wider"
            >
              Start Guardian Mode
            </button>
          ) : (
            <button 
              onClick={stopMonitoring}
              className="w-full bg-white text-gray-800 py-4 rounded-2xl font-black text-lg shadow-xl border-2 border-gray-100 active:scale-95 transition-transform uppercase tracking-wider"
            >
              Stop Monitoring
            </button>
          )}
        </div>

        {/* Footer Navigation */}
        <div className="grid grid-cols-3 gap-3 mb-24">
            <NavButton label="Debug" icon="🛠️" onClick={() => navigate("/debug")} color="bg-gray-800" />
            <NavButton label="Status" icon="📊" onClick={() => navigate("/service-status")} color="bg-indigo-600" />
            <NavButton label="Alerts" icon="📋" onClick={() => navigate("/emergency-alert")} color="bg-purple-600" />
        </div>
      </div>
    </div>
  );
}

// Sub-components for cleaner code
function PermissionTile({ icon, label, granted, required }) {
  return (
    <div className="flex items-center justify-between p-3 bg-gray-50 rounded-xl border border-gray-100">
      <div className="flex items-center gap-3">
        <span className="text-lg">{icon}</span>
        <div>
          <p className="text-xs font-bold text-gray-800">{label}</p>
          {required && <p className="text-[8px] text-red-500 font-bold uppercase">Required</p>}
        </div>
      </div>
      <div className={`h-6 w-6 rounded-full flex items-center justify-center text-[10px] ${granted ? 'bg-green-500 text-white' : 'bg-red-100 text-red-500'}`}>
        {granted ? '✓' : '!'}
      </div>
    </div>
  );
}

function NavButton({ label, icon, onClick, color }) {
  return (
    <button 
      onClick={onClick} 
      className={`${color} text-white p-4 rounded-2xl flex flex-col items-center justify-center shadow-md active:scale-90 transition-transform`}
    >
      <span className="text-xl mb-1">{icon}</span>
      <span className="text-[10px] font-bold uppercase">{label}</span>
    </button>
  );
}