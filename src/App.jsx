// // // import { Routes, Route, Navigate } from "react-router-dom";
// // // import Dashboard from "./pages/Dashboard"; // Your new landing page
// // // import Alerts from "./pages/Alerts";
// // // import Progress from "./pages/Progress"; // Assuming you'll add this next
// // // import Settings from "./pages/Settings";
// // // import { Plugins } from '@capacitor/core';
// // // import { SMSInboxReader } from '@solimanware/capacitor-sms-reader';
// // // import { SmsReader } from '@solimanware/capacitor-sms-reader';
// // // import { useEffect } from 'react';

// // // export default function App() {
// // //   const { SMSInboxReader } = Plugins;

// // //   const readMessages= async()=>{
// // //     const { smsList } = await SmsReader.getSMSList({
// // //         projection: {
// // //             selection: "", 
// // //             selectionArgs: [],
// // //             sortOrder: "date DESC" // Get latest first
// // //         }
// // //     });

    
// // //     const filteredMessages = smsList.filter(msg => 
// // //       msg.address.includes(targetNumber)
// // //     );

// // //     console.log(`Found ${filteredMessages.length} messages:`, filteredMessages);
// // //     alert(`Found ${filteredMessages.length} messages:`, filteredMessages);
// // //   }

// // //  try {
// // //    useEffect(() => {
     
// // //   const getPermissions = async () => {
// // //     //get sms permission 
// // //         const permissionStatus = await SMSInboxReader.checkPermissions();
// // //        if (permissionStatus.sms !== 'granted') {
// // //    await SMSInboxReader.requestPermissions();
// // //         }
// // //   }
   
// // //      getPermissions();
// // //      readMessages("+919899144393")
 
// // //    },[]);
// // //  } catch (error) {
// // //    console.error("Error checking/requesting SMS permissions:", error);
// // //  }

// // //   return (
// // //     <Routes>
// // //       {/* Set Dashboard as the default landing page */}
// // //       <Route path="/" element={<Dashboard />} />
      
// // //       {/* Other main application routes */}
// // //       <Route path="/alerts" element={<Alerts />} />
// // //       <Route path="/progress" element={<Progress />} />
// // //       <Route path="/settings" element={<Settings />} />

// // //       {/* Redirect any unknown paths back to Dashboard */}
// // //       <Route path="*" element={<Navigate to="/" replace />} />
// // //     </Routes>
// // //   );
// // // }

// // import Dashboard from "./pages/Dashboard";
// // import  SMSInboxReader  from '@solimanware/capacitor-sms-reader';
// // import { Plugins } from '@capacitor/core';
// // import { SMSInboxReader } from 'capacitor-sms-reader';

// // import { useEffect } from 'react';

// // export default function App() {
//   //       const { SMSInboxReader } = Plugins;
//   //   // const readMessages = async (targetNumber) => {
//     //   //   try {
//       //   //     const { smsList } = await SMSInboxReader.getSMSList({
//         //   //       projection: {
//           //   //         selection: "", 
//           //   //         selectionArgs: [],
//           //   //         sortOrder: "date DESC"
//           //   //       }
//           //   //     });
          
//           //   //     const filteredMessages = smsList.filter(msg => 
//             //   //       msg.address && msg.address.includes(targetNumber)
//             //   //     );
            
//             //   //     console.log(`Found ${filteredMessages.length} messages:`, filteredMessages);
//             //   //     // Note: alert() only takes one string argument. Use JSON.stringify for objects.
//             //   //     if (filteredMessages.length > 0) {
//               //   //       alert(`Last Message: ${filteredMessages[0].body}`);
//               //   //     }
//               //   //   } catch (err) {
//                 //   //     console.error("Read Messages Error:", err);
//                 //   //   }
//                 //   // };
                
//                 //   useEffect(() => {
//                   //     const initializeSms = async () => {
//                     //       try {
//                       //         // 1. Check/Request Permissions
//                       //         alert("Requesting SMS Permissions");
//                       //       const permissionStatus = await SMSInboxReader.checkPermissions();
//                       // if (permissionStatus.sms !== 'granted') {
//                         //   await SMSInboxReader.requestPermissions();
//                         // }
                        
//                         //         // 2. Read messages once permission is confirmed
//                         //         // await readMessages("+919899144393");
//                         //       } catch (error) {
//                           //         console.error("Initialization Error:", error);
//                           //       }
//                           //     };
                          
//                           //     initializeSms();
//                           //   }, []); // Empty dependency array runs this once on mount
                          
//                           //   return (
//                             //     <Routes>
//                             //       <Route path="/" element={<Dashboard />} />
//                             //       <Route path="/alerts" element={<Alerts />} />
//                             //       <Route path="/progress" element={<Progress />} />
//                             //       <Route path="/settings" element={<Settings />} />
//                             //       <Route path="*" element={<Navigate to="/" replace />} />
//                             //     </Routes>
//                             //   );
//                             // }
                            
//                             import React from 'react';
//                             import SmsListener from './pages/SmsReader';
//                             import SMSMonitor from './pages/SMSMonitor';
//                             import Alerts from "./pages/Alerts";
//                             import Progress from "./pages/Progress";
//                             import Settings from "./pages/Settings";
// import { Routes, Route, Navigate } from "react-router-dom";

// // Ensure this is exactly how your App.jsx looks:
// function App() {
//   return (
//     <Routes>
//       <Route path="/" element={<SMSMonitor />} />
//       <Route path="/alerts" element={<Alerts />} />
//       <Route path="/progress" element={<Progress />} />
//       <Route path="/settings" element={<Settings />} />
//       <Route path="*" element={<Navigate to="/" replace />} />
//     </Routes>
//   );
// }

// export default App;

import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { useEffect,useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { App as CapacitorApp } from '@capacitor/app';
import EmergencyAlertPage from './pages/EmergencyAlert';
import MapPage from './pages/Map';
import SMSMonitor from './pages/SMSMonitor';
import Registration from './pages/Registration';
import DebugPanel from './pages/DebugPanel';
import LocationTracker from './plugins/location-tracker';
import AppPreferences from './plugins/app-preferences';
import { Capacitor } from '@capacitor/core';
import ServiceStatus from './pages/ServiceStatus';
function AppContent() {
  const navigate = useNavigate();
   const [isRegistered, setIsRegistered] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const userPhone = localStorage.getItem('userPhone');
    setIsRegistered(!!userPhone);
    setLoading(false);
  }, []);

  useEffect(() => {
    // Auto-start location service when app opens
    if (Capacitor.getPlatform() === 'android') {
      autoStartLocationService();
    }
  }, []);

  const autoStartLocationService = async () => {
    try {
      const userId = localStorage.getItem('userId');
      const serverUrl = localStorage.getItem('serverUrl');

      if (userId && serverUrl) {
        console.log('Auto-starting location service...');
        
        // Save settings
        await AppPreferences.saveLocationSettings({ userId, serverUrl });
        
        // Check if already running
        const status = await LocationTracker.isServiceRunning();
        
        if (!status.isRunning) {
          console.log('Service not running, starting...');
          await LocationTracker.startLocationUpdates();
          console.log('✅ Location service started');
        } else {
          console.log('✅ Service already running');
        }
      }
    } catch (error) {
      console.error('Error auto-starting service:', error);
    }
  };

  
  useEffect(() => {
    // Listen for app launch from notification
    CapacitorApp.addListener('appUrlOpen', (data) => {
      if (data.url.includes('emergency')) {
        navigate('/emergency-alert');
      }
    });
  }, [navigate]);
  if (loading) {
    return <div style={{ padding: 20 }}>Loading...</div>;
  }

  return (
    <Routes>
      {/* <Route path="/" element={<SMSMonitor />} />
      <Route path="/emergency-alert" element={<EmergencyAlertPage />} />
      <Route path="/map" element={<MapPage />} /> */}
      {/* Add other routes */}
      {!isRegistered ? (
        <>
          <Route path="/register" element={<Registration />} />
          <Route path="/" element={<Registration />} />
        </>
      ) : (
        <>
         <Route path="/" element={<SMSMonitor />} />
         <Route path="/service-status" element={<ServiceStatus />} />
      <Route path="/emergency-alert" element={<EmergencyAlertPage />} />
      <Route path="/map" element={<MapPage />} />
      <Route path="/debug" element={<DebugPanel />} />
        </>
      )}
    </Routes>
  );
}

function App() {
  return (
    <Router>
      <AppContent />
    </Router>
  );
}

export default App;
