import React, { useState } from 'react';
import { User, Phone, Info, Mail, Trash2, ChevronRight, Save } from 'lucide-react';

const Settings = () => {
  // Local state for editable fields
  const [profile, setProfile] = useState({
    name: "Alex Johnson",
    phone: "9876543210"
  });

  const handleUpdate = () => {
    // Logic to save to your backend
    console.log("Profile Updated:", profile);
    alert("Profile saved successfully!");
  };

  const deactivateAccount = () => {
    const confirmed = window.confirm("Are you sure? This will delete all your 'Help' records permanently.");
    if (confirmed) {
      console.log("Deactivating...");
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 pb-10">
      {/* Header */}
      <div className="bg-white p-6 border-b border-gray-200 sticky top-0 z-10">
        <h1 className="text-2xl font-bold text-gray-800">Settings</h1>
      </div>

      <div className="p-4 max-w-md mx-auto space-y-6">
        
        {/* Profile Section */}
        <section>
          <h2 className="text-xs font-bold text-gray-400 uppercase tracking-widest ml-2 mb-2">Profile Details</h2>
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
            <div className="p-4 border-b border-gray-50 flex items-center">
              <User className="text-blue-500 mr-3" size={20} />
              <input 
                type="text" 
                value={profile.name}
                onChange={(e) => setProfile({...profile, name: e.target.value})}
                className="w-full outline-none text-gray-700 font-medium"
                placeholder="Full Name"
              />
            </div>
            <div className="p-4 flex items-center">
              <Phone className="text-green-500 mr-3" size={20} />
              <input 
                type="tel" 
                value={profile.phone}
                onChange={(e) => setProfile({...profile, phone: e.target.value})}
                className="w-full outline-none text-gray-700 font-medium"
                placeholder="Phone Number"
              />
            </div>
          </div>
          <button 
            onClick={handleUpdate}
            className="mt-3 w-full flex items-center justify-center bg-blue-600 text-white py-3 rounded-xl font-semibold active:scale-95 transition-transform"
          >
            <Save size={18} className="mr-2" /> Save Changes
          </button>
        </section>

        {/* Support Section */}
        <section>
          <h2 className="text-xs font-bold text-gray-400 uppercase tracking-widest ml-2 mb-2">Support & Info</h2>
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
            <button className="w-full p-4 flex items-center justify-between border-b border-gray-50 active:bg-gray-50">
              <div className="flex items-center">
                <Info className="text-gray-400 mr-3" size={20} />
                <span className="text-gray-700">About Us</span>
              </div>
              <ChevronRight size={18} className="text-gray-300" />
            </button>
            <button className="w-full p-4 flex items-center justify-between active:bg-gray-50">
              <div className="flex items-center">
                <Mail className="text-gray-400 mr-3" size={20} />
                <span className="text-gray-700">Contact Us</span>
              </div>
              <ChevronRight size={18} className="text-gray-300" />
            </button>
          </div>
        </section>

        {/* Danger Zone */}
        <section>
          <h2 className="text-xs font-bold text-red-400 uppercase tracking-widest ml-2 mb-2">Danger Zone</h2>
          <button 
            onClick={deactivateAccount}
            className="w-full p-4 bg-red-50 rounded-2xl border border-red-100 flex items-center justify-center text-red-600 font-bold active:bg-red-100 transition-colors"
          >
            <Trash2 size={20} className="mr-2" />
            Deactivate Account
          </button>
        </section>

      </div>
    </div>
  );
};

export default Settings;