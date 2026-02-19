const API_BASE_URL = 'http://YOUR_SERVER_URL'; // Change this to your server URL

export const apiService = {
  // Update user location
  updateUserLocation: async (userId, latitude, longitude, accuracy) => {
    try {
      const response = await fetch(`${API_BASE_URL}/api/location/update`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          userId,
          latitude,
          longitude,
          accuracy,
          timestamp: Date.now()
        })
      });
      
      return await response.json();
    } catch (error) {
      console.error('Error updating location:', error);
      throw error;
    }
  },

  // Send emergency alert
  sendEmergencyAlert: async (sender, message, latitude, longitude) => {
    try {
      const response = await fetch(`${API_BASE_URL}/api/emergency/alert`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          sender,
          message,
          latitude,
          longitude,
          timestamp: Date.now()
        })
      });
      
      return await response.json();
    } catch (error) {
      console.error('Error sending alert:', error);
      throw error;
    }
  },

  // Get nearby users (called by server when emergency occurs)
  getNearbyUsers: async (latitude, longitude, radiusKm) => {
    try {
      const response = await fetch(
        `${API_BASE_URL}/api/users/nearby?lat=${latitude}&lon=${longitude}&radius=${radiusKm}`,
        {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
          }
        }
      );
      
      return await response.json();
    } catch (error) {
      console.error('Error getting nearby users:', error);
      throw error;
    }
  },

  // Register device for push notifications
  registerDevice: async (userId, fcmToken) => {
    try {
      const response = await fetch(`${API_BASE_URL}/api/device/register`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          userId,
          fcmToken,
          timestamp: Date.now()
        })
      });
      
      return await response.json();
    } catch (error) {
      console.error('Error registering device:', error);
      throw error;
    }
  }
};