import { initializeApp } from 'firebase/app';
import { getFirestore, enableIndexedDbPersistence } from 'firebase/firestore';
import { getMessaging, getToken, onMessage } from 'firebase/messaging';

const firebaseConfig = {
  apiKey: "AIzaSyA7HbSP_wD4qRR1kqLh4x5wtRQpTCsL7lA",
  authDomain: "shee-df367.firebaseapp.com",
  projectId: "shee-df367",
  storageBucket: "shee-df367.appspot.com",
  messagingSenderId: "862329423104",
  appId: "1:862329423104:android:f428952da9f1dd014d1fe5"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);

// Initialize Firestore
const db = getFirestore(app);

// Enable offline persistence
enableIndexedDbPersistence(db).catch((err) => {
  if (err.code === 'failed-precondition') {
    console.warn('Multiple tabs open, persistence can only be enabled in one tab at a time.');
  } else if (err.code === 'unimplemented') {
    console.warn('The current browser does not support persistence.');
  }
});

// Initialize Messaging (for web)
let messaging = null;
if (typeof window !== 'undefined' && 'serviceWorker' in navigator) {
  messaging = getMessaging(app);
}

export { db, messaging, getToken, onMessage };
export default app;