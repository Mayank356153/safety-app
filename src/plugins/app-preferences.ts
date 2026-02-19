import { registerPlugin } from '@capacitor/core';

export interface AppPreferencesPlugin {
  saveLocationSettings(options: { userId: string; serverUrl: string }): Promise<{ success: boolean }>;
  getLocationSettings(): Promise<{ userId: string; serverUrl: string }>;
}

const AppPreferences = registerPlugin<AppPreferencesPlugin>('AppPreferences');
export default AppPreferences;