// import { useEffect, useState } from 'react';
// import { useNavigate } from 'react-router-dom';
// import EmergencyAlert from '../plugins/emergency-alert';

// export default function EmergencyAlertPage() {
//   const [emergencyData, setEmergencyData] = useState(null);
//   const [loading, setLoading] = useState(true);
//   const navigate = useNavigate();

//   useEffect(() => {
//     loadEmergencyData();
    
//     // Check every 2 seconds for new emergencies
//     const interval = setInterval(loadEmergencyData, 2000);
    
//     return () => clearInterval(interval);
//   }, []);

//   const loadEmergencyData = async () => {
//     try {
//       const data = await EmergencyAlert.getEmergencyData();
      
//       if (data.hasEmergency) {
//         setEmergencyData(data);
//       }
      
//       setLoading(false);
//     } catch (error) {
//       console.error('Error loading emergency:', error);
//       setLoading(false);
//     }
//   };

//   const stopAlarm = async () => {
//     try {
//       await EmergencyAlert.stopAlarm();
//       alert('Alarm stopped');
//     } catch (error) {
//       console.error('Error stopping alarm:', error);
//     }
//   };

//   const clearAlert = async () => {
//     try {
//       await EmergencyAlert.clearEmergency();
//       setEmergencyData(null);
//       alert('Emergency alert cleared');
//     } catch (error) {
//       console.error('Error clearing alert:', error);
//     }
//   };

//   const openMap = () => {
//     if (emergencyData && emergencyData.hasCoordinates) {
//       navigate(`/map?lat=${emergencyData.latitude}&lon=${emergencyData.longitude}`);
//     }
//   };

//   const callEmergency = () => {
//     if (emergencyData) {
//       // Remove non-numeric characters
//       const phoneNumber = emergencyData.sender.replace(/[^0-9]/g, '');
//       window.location.href = `tel:${phoneNumber}`;
//     }
//   };
//  const openInMaps = () => {
//     // For mobile devices - opens in native maps app
//     window.location.href = `geo:${latitude},${longitude}?q=${latitude},${longitude}`;
//   };
//   if (loading) {
//     return (
//       <div style={styles.container}>
//         <h2>Loading...</h2>
//       </div>
//     );
//   }

//   if (!emergencyData || !emergencyData.hasEmergency) {
//     return (
//       <div style={styles.container}>
//         <h2>No Active Emergency</h2>
//         <p>You will be redirected here when an emergency SMS is received.</p>
//       </div>
//     );
//   }

//   return (
//     <div style={styles.emergencyContainer}>
//       {/* Flashing Alert Banner */}
//       <div style={styles.alertBanner} className="flash-alert">
//         🚨 EMERGENCY ALERT 🚨
//       </div>

//       <div style={styles.content}>
//         <h1 style={styles.title}>EMERGENCY!</h1>
        
//         <div style={styles.infoCard}>
//           <h2>From: {emergencyData.sender}</h2>
//           <p style={styles.timestamp}>
//             {new Date(emergencyData.timestamp).toLocaleString()}
//           </p>
          
//           <div style={styles.messageBox}>
//             <strong>Message:</strong>
//             <p>{emergencyData.message}</p>
//           </div>

//           {emergencyData.hasCoordinates && (
//             <div style={styles.coordinatesBox}>
//               <h3>📍 Location Detected</h3>
//               <p>Latitude: {emergencyData.latitude}</p>
//               <p>Longitude: {emergencyData.longitude}</p>
//             </div>
//           )}
//         </div>

//         {/* Action Buttons */}
//         <div style={styles.buttonGroup}>
//           <button 
//             onClick={stopAlarm}
//             style={{...styles.button, backgroundColor: '#ff9800'}}
//           >
//             🔇 Stop Alarm
//           </button>

//           {emergencyData.hasCoordinates && (
//             <button 
//               onClick={openInMaps}
//               style={{...styles.button, backgroundColor: '#2196F3'}}
//             >
//               🗺️ Open Map & Navigate
//             </button>
//           )}

//           <button 
//             onClick={callEmergency}
//             style={{...styles.button, backgroundColor: '#4CAF50'}}
//           >
//             📞 Call {emergencyData.sender}
//           </button>

//           <button 
//             onClick={clearAlert}
//             style={{...styles.button, backgroundColor: '#f44336'}}
//           >
//             ✓ Clear Alert
//           </button>
//         </div>
//       </div>

//       <style>{`
//         @keyframes flash {
//           0%, 100% { background-color: #f44336; }
//           50% { background-color: #d32f2f; }
//         }
        
//         .flash-alert {
//           animation: flash 1s infinite;
//         }
//       `}</style>
//     </div>
//   );
// }

// const styles = {
//   emergencyContainer: {
//     minHeight: '100vh',
//     backgroundColor: '#ffebee',
//   },
//   alertBanner: {
//     padding: '20px',
//     textAlign: 'center',
//     color: 'white',
//     fontSize: '24px',
//     fontWeight: 'bold',
//     textShadow: '2px 2px 4px rgba(0,0,0,0.5)',
//   },
//   content: {
//     padding: '20px',
//   },
//   title: {
//     color: '#d32f2f',
//     textAlign: 'center',
//     fontSize: '36px',
//     margin: '20px 0',
//     textShadow: '2px 2px 4px rgba(0,0,0,0.2)',
//   },
//   infoCard: {
//     backgroundColor: 'white',
//     padding: '20px',
//     borderRadius: '10px',
//     marginBottom: '20px',
//     boxShadow: '0 4px 6px rgba(0,0,0,0.1)',
//     border: '3px solid #f44336',
//   },
//   timestamp: {
//     color: '#666',
//     fontSize: '14px',
//     marginBottom: '15px',
//   },
//   messageBox: {
//     backgroundColor: '#fff3cd',
//     padding: '15px',
//     borderRadius: '5px',
//     marginTop: '15px',
//     border: '2px solid #ffc107',
//   },
//   coordinatesBox: {
//     backgroundColor: '#e3f2fd',
//     padding: '15px',
//     borderRadius: '5px',
//     marginTop: '15px',
//     border: '2px solid #2196F3',
//   },
//   buttonGroup: {
//     display: 'flex',
//     flexDirection: 'column',
//     gap: '15px',
//   },
//   button: {
//     padding: '15px',
//     fontSize: '18px',
//     fontWeight: 'bold',
//     color: 'white',
//     border: 'none',
//     borderRadius: '8px',
//     cursor: 'pointer',
//     boxShadow: '0 2px 4px rgba(0,0,0,0.2)',
//   },
//   container: {
//     padding: '20px',
//     textAlign: 'center',
//   },
// };

import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import EmergencyAlert from '../plugins/emergency-alert';

export default function EmergencyAlertPage() {
  const [emergencyData, setEmergencyData] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    loadEmergencyData();
    
    // Check every 2 seconds for new emergencies
    const interval = setInterval(loadEmergencyData, 2000);
    
    return () => clearInterval(interval);
  }, []);

  const loadEmergencyData = async () => {
    try {
      const data = await EmergencyAlert.getEmergencyData();
      
      if (data.hasEmergency) {
        setEmergencyData(data);
      }
      
      setLoading(false);
    } catch (error) {
      console.error('Error loading emergency:', error);
      setLoading(false);
    }
  };

  const stopAlarm = async () => {
    try {
      await EmergencyAlert.stopAlarm();
      alert('Alarm and vibration stopped');
    } catch (error) {
      console.error('Error stopping alarm:', error);
    }
  };

  const clearAlert = async () => {
    try {
      await EmergencyAlert.clearEmergency();
      setEmergencyData(null);
      alert('Emergency alert cleared');
      navigate('/'); // Go back to home
    } catch (error) {
      console.error('Error clearing alert:', error);
    }
  };

  const openMap = () => {
    if (emergencyData && emergencyData.hasCoordinates) {
      // navigate(`/map?lat=${emergencyData.latitude}&lon=${emergencyData.longitude}`);
         window.location.href = `geo:${emergencyData.latitude},${emergencyData.longitude}?q=${emergencyData.latitude},${emergencyData.longitude}`;
    }
  };

  const callEmergency = () => {
    if (emergencyData) {
      const phoneNumber = emergencyData.sender.replace(/[^0-9]/g, '');
      window.location.href = `tel:${phoneNumber}`;
    }
  };

  if (loading) {
    return (
      <div style={styles.container}>
        <h2>Loading...</h2>
      </div>
    );
  }

  if (!emergencyData || !emergencyData.hasEmergency) {
    return (
      <div style={styles.container}>
        <h2>No Active Emergency</h2>
        <p>No emergency alerts at the moment.</p>
        
        <button 
          onClick={() => navigate('/')}
          style={{
            padding: 15,
            marginTop: 20,
            backgroundColor: '#2196F3',
            color: 'white',
            border: 'none',
            borderRadius: 5,
            cursor: 'pointer',
            fontSize: 16
          }}
        >
          ← Back to Home
        </button>
      </div>
    );
  }

  return (
    <div style={styles.emergencyContainer}>
      {/* Flashing Alert Banner */}
      <div style={styles.alertBanner} className="flash-alert">
        🚨 EMERGENCY ALERT 🚨
      </div>

      <div style={styles.content}>
        <h1 style={styles.title}>EMERGENCY!</h1>
        
        <div style={styles.infoCard}>
          <h2>From: {emergencyData.sender}</h2>
          <p style={styles.timestamp}>
            {new Date(emergencyData.timestamp).toLocaleString()}
          </p>
          
          <div style={styles.messageBox}>
            <strong>Message:</strong>
            <p>{emergencyData.message}</p>
          </div>

          {emergencyData.hasCoordinates && (
            <div style={styles.coordinatesBox}>
              <h3>📍 Location Detected</h3>
              <p>Latitude: {emergencyData.latitude}</p>
              <p>Longitude: {emergencyData.longitude}</p>
            </div>
          )}
        </div>

        {/* Action Buttons */}
        <div style={styles.buttonGroup}>
          <button 
            onClick={stopAlarm}
            style={{...styles.button, backgroundColor: '#ff9800'}}
          >
            🔇 Stop Alarm & Vibration
          </button>

          {emergencyData.hasCoordinates && (
            <button 
              onClick={openMap}
              style={{...styles.button, backgroundColor: '#2196F3'}}
              className="pulse-button"
            >
              🗺️ Open Map & Navigate
            </button>
          )}

          <button 
            onClick={callEmergency}
            style={{...styles.button, backgroundColor: '#4CAF50'}}
          >
            📞 Call {emergencyData.sender}
          </button>

          <button 
            onClick={clearAlert}
            style={{...styles.button, backgroundColor: '#f44336'}}
          >
            ✓ Clear Alert & Go Home
          </button>
        </div>
      </div>

      <style>{`
        @keyframes flash {
          0%, 100% { background-color: #f44336; }
          50% { background-color: #d32f2f; }
        }
        
        @keyframes pulse {
          0%, 100% { transform: scale(1); }
          50% { transform: scale(1.05); }
        }
        
        .flash-alert {
          animation: flash 1s infinite;
        }
        
        .pulse-button {
          animation: pulse 1.5s infinite;
        }
      `}</style>
    </div>
  );
}

const styles = {
  emergencyContainer: {
    minHeight: '100vh',
    backgroundColor: '#ffebee',
  },
  alertBanner: {
    padding: '20px',
    textAlign: 'center',
    color: 'white',
    fontSize: '24px',
    fontWeight: 'bold',
    textShadow: '2px 2px 4px rgba(0,0,0,0.5)',
  },
  content: {
    padding: '20px',
  },
  title: {
    color: '#d32f2f',
    textAlign: 'center',
    fontSize: '36px',
    margin: '20px 0',
    textShadow: '2px 2px 4px rgba(0,0,0,0.2)',
  },
  infoCard: {
    backgroundColor: 'white',
    padding: '20px',
    borderRadius: '10px',
    marginBottom: '20px',
    boxShadow: '0 4px 6px rgba(0,0,0,0.1)',
    border: '3px solid #f44336',
  },
  timestamp: {
    color: '#666',
    fontSize: '14px',
    marginBottom: '15px',
  },
  messageBox: {
    backgroundColor: '#fff3cd',
    padding: '15px',
    borderRadius: '5px',
    marginTop: '15px',
    border: '2px solid #ffc107',
  },
  coordinatesBox: {
    backgroundColor: '#e3f2fd',
    padding: '15px',
    borderRadius: '5px',
    marginTop: '15px',
    border: '2px solid #2196F3',
  },
  buttonGroup: {
    display: 'flex',
    flexDirection: 'column',
    gap: '15px',
  },
  button: {
    padding: '15px',
    fontSize: '18px',
    fontWeight: 'bold',
    color: 'white',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer',
    boxShadow: '0 2px 4px rgba(0,0,0,0.2)',
  },
  container: {
    padding: '20px',
    textAlign: 'center',
  },
};