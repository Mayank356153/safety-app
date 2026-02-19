import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Capacitor } from '@capacitor/core';
import LocationTracker from '../plugins/location-tracker';
import AppPreferences from '../plugins/app-preferences';
export default function Registration() {
  const [phone, setPhone] = useState('');
  const [name, setName] = useState('');
  const [loading, setLoading] = useState(false);
  const [serverUrl, setServerUrl] = useState('https://safety-app-server-6p3a.onrender.com');
  const navigate = useNavigate();

  useEffect(() => {
    const savedPhone = localStorage.getItem('userPhone');
    if (savedPhone) {
      navigate('/');
    }
  }, []);

  // const handleRegister = async () => {
  //   if (!phone.trim()) {
  //     alert('Please enter your phone number');
  //     return;
  //   }

  //   if (!name.trim()) {
  //     alert('Please enter your name');
  //     return;
  //   }

  //   const cleanPhone = phone.replace(/[^0-9+]/g, '');

  //   if (cleanPhone.length < 10) {
  //     alert('Please enter a valid phone number');
  //     return;
  //   }

  //   setLoading(true);

  //   try {
  //     // Get current location
  //     let latitude = 0;
  //     let longitude = 0;

  //     if (Capacitor.getPlatform() === 'android') {
  //       try {
  //         const permCheck = await LocationTracker.checkLocationPermission();
  //         if (!permCheck.granted) {
  //           await LocationTracker.requestLocationPermission();
  //         }
          
  //         const location = await LocationTracker.getCurrentLocation();
  //         latitude = location.latitude;
  //         longitude = location.longitude;
  //       } catch (error) {
  //         console.error('Error getting location:', error);
  //       }
  //     }

  //     // Register user on server
  //     const response = await fetch(`${serverUrl}/api/user/register`, {
  //       method: 'POST',
  //       headers: { 'Content-Type': 'application/json' },
  //       body: JSON.stringify({
  //         phone: cleanPhone,
  //         name: name,
  //         latitude: latitude,
  //         longitude: longitude
  //       })
  //     });

  //     const data = await response.json();

  //     if (data.success) {
  //       // Save locally
  //       localStorage.setItem('userPhone', cleanPhone);
  //       localStorage.setItem('userName', name);
  //       localStorage.setItem('userId', cleanPhone);
  //       localStorage.setItem('serverUrl', serverUrl);
  //       localStorage.setItem('registrationDate', new Date().toISOString());

  //       // Save for Android services
  //       if (Capacitor.getPlatform() === 'android') {
  //         const prefs = window.localStorage;
  //         prefs.setItem('userId', cleanPhone);
  //         prefs.setItem('serverUrl', serverUrl);
  //       }

  //       alert(`✅ Registration Complete!\n\nPhone: ${cleanPhone}\nName: ${name}\n\nYou will now receive alerts when you're within 2km of any emergency.`);
  //       window.location.reload();
  //       navigate('/');
  //     } else {
  //       alert('❌ Registration failed: ' + data.error);
  //     }
  //   } catch (error) {
  //     console.error('Error registering:', error);
  //     alert('❌ Error: ' + error.message);
  //   } finally {
  //     setLoading(false);
  //   }
  // };

  const handleRegister = async () => {
  if (!phone.trim() || !name.trim()) {
    alert('Please enter name and phone');
    return;
  }

  const cleanPhone = phone.replace(/[^0-9+]/g, '');

  if (cleanPhone.length < 10) {
    alert('Invalid phone number');
    return;
  }

  setLoading(true);

  try {
    console.log('🔄 Starting registration...');
    
    // Get current location
    let latitude = 0;
    let longitude = 0;

    if (Capacitor.getPlatform() === 'android') {
      try {
        console.log('📍 Requesting location permission...');
        const permCheck = await LocationTracker.checkLocationPermission();
        
        if (!permCheck.granted) {
          const permResult = await LocationTracker.requestLocationPermission();
          if (!permResult.granted) {
            alert('Location permission required for registration');
            setLoading(false);
            return;
          }
        }
        
        console.log('📍 Getting current location...');
        const location = await LocationTracker.getCurrentLocation();
        latitude = location.latitude;
        longitude = location.longitude;
        console.log(`✅ Location: ${latitude}, ${longitude}`);
      } catch (error) {
        console.error('Error getting location:', error);
        alert('Could not get location. Please enable GPS and try again.');
        setLoading(false);
        return;
      }
    }

    console.log('🌐 Sending to server:', serverUrl);
    
    // Register on server
    const response = await fetch(`${serverUrl}/api/user/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        phone: cleanPhone,
        name: name,
        latitude: latitude,
        longitude: longitude
      })
    });

    console.log('📡 Response status:', response.status);
    
    if (!response.ok) {
      throw new Error(`Server error: ${response.status}`);
    }

    const data = await response.json();
    console.log('📥 Response data:', data);

    if (data.success) {
      // Save to localStorage
      localStorage.setItem('userPhone', cleanPhone);
      localStorage.setItem('userName', name);
      localStorage.setItem('userId', cleanPhone);
      localStorage.setItem('serverUrl', serverUrl);
      localStorage.setItem('registrationDate', new Date().toISOString());

      console.log('✅ Saved to localStorage');

      // IMPORTANT: Save to Android SharedPreferences for background service
      if (Capacitor.getPlatform() === 'android') {
        console.log('💾 Saving to Android SharedPreferences...');
        
         
        await AppPreferences.saveLocationSettings({
          userId: cleanPhone,
          serverUrl: serverUrl
        });
        
        // We need to use the native plugin to save this
        // For now, we'll create a helper function
        await saveToNativeStorage(cleanPhone, serverUrl);

      }

      alert(`✅ Registration Complete!\n\nPhone: ${cleanPhone}\nName: ${name}\nLocation: ${latitude}, ${longitude}\n\nLocation tracking will start automatically.`);
      window.location.reload();
    } else {
      throw new Error(data.error || 'Registration failed');
    }
  } catch (error) {
    console.error('❌ Registration error:', error);
    alert(`❌ Error: ${error.message}\n\nPlease check:\n1. Server is running\n2. Server URL is correct\n3. You're on the same WiFi`);
  } finally {
    setLoading(false);
  }
};

// Helper function to save to native storage
const saveToNativeStorage = async (userId, serverUrl) => {
  try {
    // This will be picked up by the Android service
    const script = document.createElement('script');
    script.textContent = `
      (function() {
        if (window.Android) {
          window.Android.savePreference('userId', '${userId}');
          window.Android.savePreference('serverUrl', '${serverUrl}');
        }
      })();
    `;
    document.body.appendChild(script);
    document.body.removeChild(script);
  } catch (error) {
    console.error('Error saving to native storage:', error);
  }
};
  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <div style={styles.icon}>🛡️</div>
        <h1 style={styles.title}>Safety Alert App</h1>
        <p style={styles.subtitle}>One-time Registration</p>

        <div style={styles.form}>
          <label style={styles.label}>Your Name</label>
          <input
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="Enter your full name"
            style={styles.input}
          />

          <label style={styles.label}>Phone Number</label>
          <input
            type="tel"
            value={phone}
            onChange={(e) => setPhone(e.target.value)}
            placeholder="+919899144393"
            style={styles.input}
          />
          <p style={styles.hint}>Include country code (e.g., +91 for India)</p>

          
          <button
            onClick={handleRegister}
            disabled={loading}
            style={{
              ...styles.button,
              backgroundColor: loading ? '#ccc' : '#4CAF50',
              cursor: loading ? 'not-allowed' : 'pointer'
            }}
          >
            {loading ? 'Registering...' : '✓ Register'}
          </button>
        </div>

        <div style={styles.infoBox}>
          <p style={styles.infoText}>
            ℹ️ How it works:
          </p>
          <ul style={styles.infoList}>
            <li>Your location updates every 2-3 minutes</li>
            <li>When emergency SMS arrives, server finds nearby users</li>
            <li>If you're within 2km, you'll get instant alert</li>
            <li>Alarm + vibration + notification automatically</li>
          </ul>
        </div>
      </div>
    </div>
  );
}

const styles = {
  container: {
    minHeight: '100vh',
    backgroundColor: '#f5f5f5',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20
  },
  card: {
    backgroundColor: 'white',
    borderRadius: 16,
    padding: 30,
    maxWidth: 450,
    width: '100%',
    boxShadow: '0 4px 12px rgba(0,0,0,0.1)'
  },
  icon: {
    fontSize: 60,
    textAlign: 'center',
    marginBottom: 10
  },
  title: {
    textAlign: 'center',
    color: '#333',
    marginBottom: 5,
    fontSize: 24
  },
  subtitle: {
    textAlign: 'center',
    color: '#666',
    marginBottom: 30,
    fontSize: 14
  },
  form: {
    marginBottom: 20
  },
  label: {
    display: 'block',
    marginBottom: 8,
    fontWeight: 'bold',
    color: '#333',
    fontSize: 14
  },
  input: {
    width: '100%',
    padding: 12,
    fontSize: 16,
    borderRadius: 8,
    border: '2px solid #ddd',
    marginBottom: 15,
    boxSizing: 'border-box',
    outline: 'none'
  },
  hint: {
    fontSize: 12,
    color: '#999',
    marginTop: -10,
    marginBottom: 20
  },
  button: {
    width: '100%',
    padding: 15,
    fontSize: 18,
    fontWeight: 'bold',
    color: 'white',
    border: 'none',
    borderRadius: 8,
    marginTop: 10
  },
  infoBox: {
    backgroundColor: '#e3f2fd',
    padding: 15,
    borderRadius: 8,
    border: '1px solid #2196F3'
  },
  infoText: {
    margin: 0,
    marginBottom: 10,
    fontSize: 13,
    fontWeight: 'bold',
    color: '#1976D2'
  },
  infoList: {
    margin: 0,
    paddingLeft: 20,
    fontSize: 13,
    color: '#555',
    lineHeight: 1.8
  }
};