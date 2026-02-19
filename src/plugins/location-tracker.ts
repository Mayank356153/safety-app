import { registerPlugin } from '@capacitor/core';

export interface LocationTrackerPlugin {
  checkLocationPermission(): Promise<{ granted: boolean }>;
  requestLocationPermission(): Promise<{ granted: boolean }>;
  getCurrentLocation(): Promise<LocationData>;
  startLocationUpdates(): Promise<{ success: boolean; message: string }>;
  stopLocationUpdates(): Promise<{ success: boolean }>;
  isServiceRunning(): Promise<{ isRunning: boolean }>;  // ADD THIS
}
export interface LocationData {
  latitude: number;
  longitude: number;
  accuracy: number;
  timestamp: number;
}

const LocationTracker = registerPlugin<LocationTrackerPlugin>('LocationTracker');

export default LocationTracker;