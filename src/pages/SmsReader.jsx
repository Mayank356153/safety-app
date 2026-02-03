// import { useEffect, useState } from 'react';
// import { Capacitor } from '@capacitor/core';
// import SMSReader from '../plugins/sms-reader';

// export default function SmsListener() {
//   const [messages, setMessages] = useState([]);
//   const [hasPermission, setHasPermission] = useState(false);
//   const [loading, setLoading] = useState(false);

//   useEffect(() => {
//     if (Capacitor.getPlatform() === 'android') {
//       checkPermissions();
//     }
//   }, []);

//   const checkPermissions = async () => {
//     try {
//       alert("Checking permissions...");
//       const result = await SMSReader.checkPermissions();
//       alert(`Permission status: ${result.sms}`);
      
//       const granted = result.sms === 'granted';
//       setHasPermission(granted);
      
//       if (!granted) {
//         alert("Permission not granted. Click 'Request Permissions' button.");
//       }
      
//       return granted;
//     } catch (error) {
//       console.error('Error checking permissions:', error);
//       alert('Error checking permissions: ' + error.message);
//       return false;
//     }
//   };

//   const requestPermissions = async () => {
//     try {
//       alert("Requesting SMS permissions...");
//       const result = await SMSReader.requestPermissions();
//       alert(`Permission result: ${result.sms}`);
      
//       const granted = result.sms === 'granted';
//       setHasPermission(granted);
      
//       if (granted) {
//         alert("Permission granted! You can now read SMS.");
//       } else {
//         alert("Permission denied. Please enable SMS permissions in app settings.");
//       }
      
//       return granted;
//     } catch (error) {
//       console.error('Error requesting permissions:', error);
//       alert('Error: ' + error.message);
//       return false;
//     }
//   };

//   const readSMS = async () => {
//     setLoading(true);
    
//     try {
//       // Check permission first
//       if (!hasPermission) {
//         const granted = await requestPermissions();
//         if (!granted) {
//           setLoading(false);
//           return;
//         }
//       }

//       alert("Reading SMS messages...");
//       const result = await SMSReader.getMessages({
//         box: 'inbox',
//         maxCount: 50
//       });

//       if (result && result.messages) {
//         setMessages(result.messages);
//         alert(`Successfully loaded ${result.messages.length} messages`);
//       } else {
//         alert('No messages found');
//       }
//     } catch (error) {
//       console.error('Error reading SMS:', error);
//       alert('Error reading SMS: ' + error.message);
//     } finally {
//       setLoading(false);
//     }
//   };

//   if (Capacitor.getPlatform() !== 'android') {
//     return (
//       <div style={{ padding: 16 }}>
//         <h2>SMS Reader</h2>
//         <p style={{ color: 'red' }}>
//           SMS reading only works on Android devices.
//           <br />
//           Current platform: {Capacitor.getPlatform()}
//         </p>
//       </div>
//     );
//   }

//   return (
//     <div style={{ padding: 16 }}>
//       <h2>IoT SMS Listener</h2>

//       <div style={{ marginBottom: 20 }}>
//         <p>
//           <strong>Permission Status:</strong>{' '}
//           <span style={{ color: hasPermission ? 'green' : 'red' }}>
//             {hasPermission ? '✓ Granted' : '✗ Not Granted'}
//           </span>
//         </p>
//       </div>

//       <div style={{ marginBottom: 20 }}>
//         <button 
//           onClick={checkPermissions}
//           style={{
//             padding: '10px 20px',
//             marginRight: 10,
//             backgroundColor: '#2196F3',
//             color: 'white',
//             border: 'none',
//             borderRadius: 5,
//             cursor: 'pointer'
//           }}
//         >
//           Check Permissions
//         </button>

//         <button 
//           onClick={requestPermissions}
//           style={{
//             padding: '10px 20px',
//             marginRight: 10,
//             backgroundColor: '#FF9800',
//             color: 'white',
//             border: 'none',
//             borderRadius: 5,
//             cursor: 'pointer'
//           }}
//         >
//           Request Permissions
//         </button>

//         <button 
//           onClick={readSMS}
//           disabled={loading}
//           style={{
//             padding: '10px 20px',
//             backgroundColor: loading ? '#ccc' : '#4CAF50',
//             color: 'white',
//             border: 'none',
//             borderRadius: 5,
//             cursor: loading ? 'not-allowed' : 'pointer'
//           }}
//         >
//           {loading ? 'Loading...' : 'Read SMS Messages'}
//         </button>
//       </div>

//       {messages.length === 0 ? (
//         <p>No SMS messages loaded yet. Click "Read SMS Messages" to load your inbox.</p>
//       ) : (
//         <div>
//           <h3>Messages ({messages.length})</h3>
//           <div style={{ maxHeight: 500, overflowY: 'auto' }}>
//             {messages.map((msg) => (
//               <div 
//                 key={msg.id}
//                 style={{
//                   border: '1px solid #ddd',
//                   padding: 15,
//                   marginBottom: 10,
//                   borderRadius: 5,
//                   backgroundColor: msg.type === 1 ? '#e3f2fd' : '#f1f8e9'
//                 }}
//               >
//                 <p style={{ margin: '5px 0' }}>
//                   <strong>{msg.type === 1 ? 'From:' : 'To:'}</strong> {msg.address}
//                 </p>
//                 <p style={{ margin: '5px 0' }}>
//                   <strong>Message:</strong> {msg.body}
//                 </p>
//                 <p style={{ fontSize: 12, color: '#666', margin: '5px 0' }}>
//                   <strong>Date:</strong> {new Date(msg.date).toLocaleString()}
//                 </p>
//               </div>
//             ))}
//           </div>
//         </div>
//       )}
//     </div>
//   );
// }

import { useEffect, useState } from 'react';
import { Capacitor } from '@capacitor/core';
import SMSReader from '../plugins/sms-reader';
import SMSReceiver from '../plugins/sms-receiver'; // Use the receiver plugin for monitoring

export default function SmsListener() {
  const [messages, setMessages] = useState([]);
  const [hasPermission, setHasPermission] = useState(false);
  const [loading, setLoading] = useState(false);
  const [isMonitoring, setIsMonitoring] = useState(false);

  useEffect(() => {
    if (Capacitor.getPlatform() === 'android') {
      checkPermissions();
    }
  }, []);

  const checkPermissions = async () => {
    try {
      const result = await SMSReader.checkPermissions();
      const granted = result.sms === 'granted';
      setHasPermission(granted);
      return granted;
    } catch (error) {
      console.error('Error checking permissions:', error);
      return false;
    }
  };

  const requestPermissions = async () => {
    try {
      const result = await SMSReader.requestPermissions();
      const granted = result.sms === 'granted';
      setHasPermission(granted);
      return granted;
    } catch (error) {
      alert('Error: ' + error.message);
      return false;
    }
  };

  // NEW: Request Overlay Permission
  const requestOverlay = async () => {
    try {
      await SMSReader.requestOverlayPermission();
      alert("Please enable 'Display over other apps' in the settings that just opened.");
    } catch (error) {
      alert("Overlay Error: " + error.message);
    }
  };

  const readSMS = async () => {
    setLoading(true);
    try {
      if (!hasPermission) {
        const granted = await requestPermissions();
        if (!granted) return;
      }
      const result = await SMSReader.getMessages({ box: 'inbox', maxCount: 50 });
      if (result && result.messages) {
        setMessages(result.messages);
      }
    } catch (error) {
      alert('Error reading SMS: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  if (Capacitor.getPlatform() !== 'android') {
    return <div style={{ padding: 16 }}><h2>SMS Reader</h2><p>Android only.</p></div>;
  }

  return (
    <div style={{ padding: 16, fontFamily: 'sans-serif' }}>
      <h2>IoT SMS Listener & Alert System</h2>

      {/* Permission & Status Cards */}
      <div style={{ display: 'grid', gap: '10px', marginBottom: 20 }}>
        <div style={{ padding: 15, backgroundColor: '#f5f5f5', borderRadius: 8 }}>
          <strong>SMS Permission:</strong>{' '}
          <span style={{ color: hasPermission ? 'green' : 'red' }}>
            {hasPermission ? '✓ Granted' : '✗ Denied'}
          </span>
        </div>
        
        {/* NEW Overlay Status Information */}
        <div style={{ padding: 15, backgroundColor: '#e8eaf6', borderRadius: 8, borderLeft: '5px solid #3f51b5' }}>
          <strong>Background Alert Status:</strong> Ready for Overlay
          <p style={{ fontSize: '12px', margin: '5px 0 0' }}>Allows the app to show alerts even when closed.</p>
        </div>
      </div>

      {/* Action Buttons */}
      <div style={{ marginBottom: 20, display: 'flex', flexWrap: 'wrap', gap: '10px' }}>
        <button onClick={requestOverlay} style={btnStyle('#673AB7')}>
          🖼️ Enable Overlay Alert
        </button>

        <button onClick={requestPermissions} style={btnStyle('#FF9800')}>
          🔐 SMS Permissions
        </button>

        <button onClick={readSMS} disabled={loading} style={btnStyle(loading ? '#ccc' : '#4CAF50')}>
          {loading ? 'Loading...' : '🔄 Refresh Inbox'}
        </button>
      </div>

      <hr />

      {/* Messages List */}
      <div style={{ marginTop: 20 }}>
        <h3>Latest Received Messages</h3>
        {messages.length === 0 ? (
          <p style={{ color: '#666' }}>No messages found. Ensure the monitored device is sending coordinates.</p>
        ) : (
          <div style={{ maxHeight: 500, overflowY: 'auto' }}>
            {messages.map((msg) => (
              <div 
                key={msg.id}
                style={{
                  border: '1px solid #ddd',
                  padding: 15,
                  marginBottom: 10,
                  borderRadius: 8,
                  backgroundColor: msg.body.includes('Alert') ? '#ffebee' : '#f9f9f9',
                  borderLeft: msg.body.includes('Alert') ? '5px solid #f44336' : '1px solid #ddd'
                }}
              >
                <p style={{ margin: '0 0 5px' }}><strong>From:</strong> {msg.address}</p>
                <p style={{ margin: '0 0 5px', fontSize: '16px' }}><strong>Message:</strong> {msg.body}</p>
                <p style={{ fontSize: 12, color: '#666', margin: 0 }}>
                  {new Date(msg.date).toLocaleString()}
                </p>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

const btnStyle = (color) => ({
  padding: '12px 20px',
  backgroundColor: color,
  color: 'white',
  border: 'none',
  borderRadius: 6,
  cursor: 'pointer',
  fontWeight: 'bold',
  fontSize: '14px'
});