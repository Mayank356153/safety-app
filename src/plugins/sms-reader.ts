import { registerPlugin } from '@capacitor/core';

export interface SMSReaderPlugin {
  checkPermissions(): Promise<{ sms: string }>;
  requestPermissions(): Promise<{ sms: string }>;
  getMessages(options: { box?: string; maxCount?: number }): Promise<{ messages: SMSMessage[] }>;
}

export interface SMSMessage {
  id: string;
  address: string;
  body: string;
  date: number;
  type: number; // 1 = received, 2 = sent
}

const SMSReader = registerPlugin<SMSReaderPlugin>('SMSReader');

export default SMSReader;