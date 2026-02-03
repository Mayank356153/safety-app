import { registerPlugin } from '@capacitor/core';

export interface EmergencyAlertPlugin {
  getEmergencyData(): Promise<EmergencyData>;
  clearEmergency(): Promise<{ success: boolean }>;
  stopAlarm(): Promise<{ success: boolean }>;
}

export interface EmergencyData {
  hasEmergency: boolean;
  sender?: string;
  message?: string;
  timestamp?: number;
  hasCoordinates?: boolean;
  latitude?: string;
  longitude?: string;
}

const EmergencyAlert = registerPlugin<EmergencyAlertPlugin>('EmergencyAlert');

export default EmergencyAlert;