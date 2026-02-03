import { registerPlugin } from '@capacitor/core';

export interface PermissionsPlugin {
  checkAllPermissions(): Promise<PermissionStatus>;
  requestAllPermissions(): Promise<PermissionStatus>;
  checkOverlayPermission(): Promise<{ granted: boolean }>;
  requestOverlayPermission(): Promise<{ success: boolean; message?: string; granted?: boolean }>;
  openAppSettings(): Promise<{ success: boolean }>;
  checkNotificationPermission(): Promise<{ granted: boolean }>;
  requestNotificationPermission(): Promise<{ granted: boolean }>;
}

export interface PermissionStatus {
  sms?: boolean;
  notifications?: boolean;
  overlay?: boolean;
  vibrate?: boolean;
  wakelock?: boolean;
  audio?: boolean;
  granted?: boolean;
}

const Permissions = registerPlugin<PermissionsPlugin>('Permissions');

export default Permissions;