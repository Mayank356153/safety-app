import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';

export default function MapPage() {
  const [searchParams] = useSearchParams();
  const [latitude, setLatitude] = useState('');
  const [longitude, setLongitude] = useState('');

  useEffect(() => {
    const lat = searchParams.get('lat');
    const lon = searchParams.get('lon');
    
    if (lat && lon) {
      setLatitude(lat);
      setLongitude(lon);
    }
  }, [searchParams]);

  const openGoogleMaps = () => {
    const url = `https://www.google.com/maps/dir/?api=1&destination=${latitude},${longitude}`;
    window.open(url, '_blank');
  };

  const openInMaps = () => {
    // For mobile devices - opens in native maps app
    window.location.href = `geo:${latitude},${longitude}?q=${latitude},${longitude}`;
  };

  if (!latitude || !longitude) {
    return (
      <div style={styles.container}>
        <h2>No location data available</h2>
      </div>
    );
  }

  return (
    <div style={styles.container}>
      <h1>📍 Emergency Location</h1>
      
      <div style={styles.infoBox}>
        <p><strong>Latitude:</strong> {latitude}</p>
        <p><strong>Longitude:</strong> {longitude}</p>
      </div>

      {/* Embedded Map */}
      <div style={styles.mapContainer}>
        <iframe
          width="100%"
          height="400"
          frameBorder="0"
          style={{ border: 0, borderRadius: '10px' }}
          src={`https://www.google.com/maps/embed/v1/place?key=YOUR_GOOGLE_MAPS_API_KEY&q=${latitude},${longitude}`}
          allowFullScreen
        ></iframe>
      </div>

      <div style={styles.buttonGroup}>
        <button onClick={openGoogleMaps} style={styles.button}>
          🗺️ Open in Google Maps
        </button>
        
        <button onClick={openInMaps} style={styles.navigationButton}>
          🧭 Start Navigation
        </button>
      </div>

      {/* Alternative: Show coordinates link */}
      <div style={styles.linkBox}>
        <p>Alternative links:</p>
        <a 
          href={`https://www.google.com/maps?q=${latitude},${longitude}`}
          target="_blank"
          rel="noopener noreferrer"
          style={styles.link}
        >
          View on Google Maps
        </a>
        <br />
        <a 
          href={`https://maps.apple.com/?q=${latitude},${longitude}`}
          target="_blank"
          rel="noopener noreferrer"
          style={styles.link}
        >
          View on Apple Maps
        </a>
      </div>
    </div>
  );
}

const styles = {
  container: {
    padding: '20px',
  },
  infoBox: {
    backgroundColor: '#e3f2fd',
    padding: '15px',
    borderRadius: '8px',
    marginBottom: '20px',
  },
  mapContainer: {
    marginBottom: '20px',
  },
  buttonGroup: {
    display: 'flex',
    flexDirection: 'column',
    gap: '10px',
    marginBottom: '20px',
  },
  button: {
    padding: '15px',
    fontSize: '16px',
    backgroundColor: '#2196F3',
    color: 'white',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer',
  },
  navigationButton: {
    padding: '15px',
    fontSize: '16px',
    backgroundColor: '#4CAF50',
    color: 'white',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer',
  },
  linkBox: {
    backgroundColor: '#f5f5f5',
    padding: '15px',
    borderRadius: '8px',
  },
  link: {
    color: '#2196F3',
    textDecoration: 'none',
    fontSize: '16px',
  },
};