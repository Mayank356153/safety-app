import { registerPlugin } from '@capacitor/core';

export interface SMSReceiverPlugin {
  checkPermissions(): Promise<{ sms: string }>;
  requestPermissions(): Promise<{ sms: string }>;
  requestOverlayPermission(): Promise<void>;
  startListening(options: { phoneNumber: string }): Promise<{ success: boolean; message: string }>;
  stopListening(): Promise<{ success: boolean; message: string }>;
  setMonitoredNumber(options: { phoneNumber: string }): Promise<{ success: boolean; phoneNumber: string }>;
  getMonitoredNumber(): Promise<{ phoneNumber: string }>;
}

const SMSReceiver = registerPlugin<SMSReceiverPlugin>('SMSReceiver');

export default SMSReceiver;