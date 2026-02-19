import { db } from '../config/firebase';
import { 
  collection, 
  doc, 
  setDoc, 
  getDoc,
  getDocs,
  query, 
  where,
  onSnapshot,
  serverTimestamp,
  GeoPoint,
  orderBy,
  limit
} from 'firebase/firestore';

export const firebaseService = {
  // Update user location in Firestore
  async updateUserLocation(userId, latitude, longitude, accuracy) {
    try {
      const userLocationRef = doc(db, 'userLocations', userId);
      
      await setDoc(userLocationRef, {
        userId,
        location: new GeoPoint(parseFloat(latitude), parseFloat(longitude)),
        latitude: parseFloat(latitude),
        longitude: parseFloat(longitude),
        accuracy: accuracy || 0,
        timestamp: serverTimestamp(),
        lastUpdated: Date.now()
      }, { merge: true });

      console.log('Location updated in Firebase:', userId, latitude, longitude);
      return { success: true };
    } catch (error) {
      console.error('Error updating location:', error);
      throw error;
    }
  },

  // Get user's current location
  async getUserLocation(userId) {
    try {
      const userLocationRef = doc(db, 'userLocations', userId);
      const docSnap = await getDoc(userLocationRef);
      
      if (docSnap.exists()) {
        return docSnap.data();
      } else {
        return null;
      }
    } catch (error) {
      console.error('Error getting user location:', error);
      throw error;
    }
  },

  // Listen to user location updates in real-time
  subscribeToUserLocation(userId, callback) {
    const userLocationRef = doc(db, 'userLocations', userId);
    
    return onSnapshot(userLocationRef, (doc) => {
      if (doc.exists()) {
        callback(doc.data());
      }
    });
  },

  // Send emergency alert
  async sendEmergencyAlert(alertData) {
    try {
      const alertRef = doc(collection(db, 'emergencyAlerts'));
      
      const alert = {
        alertId: alertRef.id,
        sender: alertData.sender,
        senderId: alertData.senderId || 'unknown',
        message: alertData.message,
        location: new GeoPoint(
          parseFloat(alertData.latitude), 
          parseFloat(alertData.longitude)
        ),
        latitude: parseFloat(alertData.latitude),
        longitude: parseFloat(alertData.longitude),
        timestamp: serverTimestamp(),
        createdAt: Date.now(),
        resolved: false,
        notifiedUsers: []
      };

      await setDoc(alertRef, alert);
      
      console.log('Emergency alert saved to Firebase:', alertRef.id);
      
      // Find and notify nearby users
      const nearbyUsers = await this.findNearbyUsers(
        alertData.latitude, 
        alertData.longitude,
        alertData.senderId
      );
      
      return {
        success: true,
        alertId: alertRef.id,
        nearbyUsers: nearbyUsers
      };
    } catch (error) {
      console.error('Error sending emergency alert:', error);
      throw error;
    }
  },

  // Find nearby users using geohashing approach
  async findNearbyUsers(latitude, longitude, excludeUserId) {
    try {
      const lat = parseFloat(latitude);
      const lon = parseFloat(longitude);
      
      let radiusKm = 2; // Start with 2km
      const maxRadius = 10; // Max 10km
      const minUsers = 3; // Minimum 3 users
      let nearbyUsers = [];

      while (nearbyUsers.length < minUsers && radiusKm <= maxRadius) {
        console.log(`Searching for users within ${radiusKm}km...`);
        
        // Calculate bounding box
        const latDelta = radiusKm / 111; // 1 degree latitude ≈ 111km
        const lonDelta = radiusKm / (111 * Math.cos(lat * Math.PI / 180));

        const minLat = lat - latDelta;
        const maxLat = lat + latDelta;
        const minLon = lon - lonDelta;
        const maxLon = lon + lonDelta;

        // Query Firestore
        const usersRef = collection(db, 'userLocations');
        const q = query(
          usersRef,
          where('latitude', '>=', minLat),
          where('latitude', '<=', maxLat)
        );

        const querySnapshot = await getDocs(q);
        
        nearbyUsers = [];
        querySnapshot.forEach((doc) => {
          const userData = doc.data();
          
          // Skip the sender
          if (userData.userId === excludeUserId) return;
          
          // Filter by longitude and calculate actual distance
          if (userData.longitude >= minLon && userData.longitude <= maxLon) {
            const distance = this.calculateDistance(
              lat, lon,
              userData.latitude, userData.longitude
            );
            
            if (distance <= radiusKm) {
              nearbyUsers.push({
                userId: userData.userId,
                latitude: userData.latitude,
                longitude: userData.longitude,
                distance: distance,
                fcmToken: userData.fcmToken || null
              });
            }
          }
        });

        nearbyUsers.sort((a, b) => a.distance - b.distance);

        console.log(`Found ${nearbyUsers.length} users within ${radiusKm}km`);

        if (nearbyUsers.length < minUsers && radiusKm < maxRadius) {
          radiusKm += 1; // Increase by 1km
        } else {
          break;
        }
      }

      // Save nearby users to alert document
      if (nearbyUsers.length > 0) {
        console.log('Nearby users:', nearbyUsers);
      }

      return nearbyUsers;
    } catch (error) {
      console.error('Error finding nearby users:', error);
      return [];
    }
  },

  // Calculate distance between two coordinates (Haversine formula)
  calculateDistance(lat1, lon1, lat2, lon2) {
    const R = 6371; // Earth's radius in km
    const dLat = (lat2 - lat1) * Math.PI / 180;
    const dLon = (lon2 - lon1) * Math.PI / 180;
    const a = 
      Math.sin(dLat/2) * Math.sin(dLat/2) +
      Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
      Math.sin(dLon/2) * Math.sin(dLon/2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    return R * c;
  },

  // Get active emergency alerts
  async getActiveAlerts() {
    try {
      const alertsRef = collection(db, 'emergencyAlerts');
      const q = query(
        alertsRef, 
        where('resolved', '==', false),
        orderBy('createdAt', 'desc'),
        limit(10)
      );
      
      const querySnapshot = await getDocs(q);
      const alerts = [];
      
      querySnapshot.forEach((doc) => {
        alerts.push({
          id: doc.id,
          ...doc.data()
        });
      });
      
      return alerts;
    } catch (error) {
      console.error('Error getting active alerts:', error);
      return [];
    }
  },

  // Subscribe to real-time alerts
  subscribeToAlerts(callback) {
    const alertsRef = collection(db, 'emergencyAlerts');
    const q = query(
      alertsRef,
      where('resolved', '==', false),
      orderBy('createdAt', 'desc')
    );

    return onSnapshot(q, (snapshot) => {
      const alerts = [];
      snapshot.forEach((doc) => {
        alerts.push({
          id: doc.id,
          ...doc.data()
        });
      });
      callback(alerts);
    });
  },

  // Mark alert as resolved
  async resolveAlert(alertId) {
    try {
      const alertRef = doc(db, 'emergencyAlerts', alertId);
      await setDoc(alertRef, {
        resolved: true,
        resolvedAt: serverTimestamp()
      }, { merge: true });
      
      return { success: true };
    } catch (error) {
      console.error('Error resolving alert:', error);
      throw error;
    }
  },

  // Save FCM token for push notifications
  async saveFCMToken(userId, fcmToken) {
    try {
      const userLocationRef = doc(db, 'userLocations', userId);
      await setDoc(userLocationRef, {
        fcmToken: fcmToken,
        userId: userId
      }, { merge: true });
      
      console.log('FCM token saved for user:', userId);
      return { success: true };
    } catch (error) {
      console.error('Error saving FCM token:', error);
      throw error;
    }
  }
};