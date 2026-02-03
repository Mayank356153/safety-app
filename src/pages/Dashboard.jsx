import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Settings, Link, Trophy, MapPin, Bell, XCircle } from 'lucide-react';
import AlertCard from '../components/AlertCard'; // Ensure the path to your AlertCard is correct

const Dashboard = () => {
  const navigate = useNavigate();
  
  // State to manage the visibility of the test alert
  const [showAlert, setShowAlert] = useState(false);

  const userName = "Alex";
  const helpCount = 12;

  // Mock data for the alert
  const mockAlert = {
    id: "alert-123",
    distance: "1.2",
    victimName: "Unknown User",
    time: "Just Now"
  };

  const menuItems = [
    { id: 1, title: "Link My Device", desc: "Connect hardware tracker", icon: <Link className="text-blue-500" />, color: "bg-blue-50", path: "/link-device" },
    { id: 2, title: "My Progress", desc: `${helpCount} helps completed`, icon: <Trophy className="text-amber-500" />, color: "bg-amber-50", path: "/progress" },
    { id: 3, title: "Settings", desc: "App config", icon: <Settings className="text-gray-600" />, color: "bg-gray-100", path: "/settings" }
  ];

  return (
    <div className="relative min-h-screen bg-white p-6 font-sans">
      
      {/* 1. TEST ALERT OVERLAY */}
      {showAlert && (
        <div className="fixed inset-0 z-[100] flex items-center justify-center p-6 bg-black/60 backdrop-blur-sm">
          <div className="w-full max-w-sm animate-in fade-in zoom-in duration-300">
            <div className="flex justify-end mb-2">
              <button onClick={() => setShowAlert(false)} className="text-white flex items-center text-sm bg-white/20 px-3 py-1 rounded-full">
                <XCircle size={16} className="mr-1" /> Close Preview
              </button>
            </div>
            {/* Using the AlertCard component we built earlier */}
            <AlertCard alert={mockAlert} />
          </div>
        </div>
      )}

      <header className="mb-8 mt-4">
        <p className="text-gray-500 text-lg">Hi,</p>
        <h1 className="text-3xl font-bold text-gray-900">{userName}!</h1>
      </header>

      {/* 2. TEMPORARY TEST BUTTON */}
      <div className="mb-6">
        <button 
          onClick={() => setShowAlert(true)}
          className="w-full bg-amber-100 border-2 border-dashed border-amber-400 text-amber-700 py-3 rounded-xl font-bold flex items-center justify-center active:scale-95 transition-transform"
        >
          <Bell className="mr-2 animate-ring" size={20} />
          TEST: Simulate Incoming Alert
        </button>
      </div>

      <div 
        onClick={() => navigate('/alerts')} 
        className="bg-red-500 rounded-2xl p-4 mb-8 flex items-center justify-between text-white shadow-lg cursor-pointer active:scale-95 transition-transform"
      >
        <div>
          <h2 className="font-semibold text-lg">System Active</h2>
          <p className="text-red-100 text-sm">Monitoring 9555769448</p>
        </div>
        <div className="bg-white/20 p-2 rounded-full">
          <Bell size={24} />
        </div>
      </div>

      <div className="grid grid-cols-1 gap-4">
        {menuItems.map((item) => (
          <button
            key={item.id}
            className={`flex items-center p-5 rounded-2xl transition-transform active:scale-95 ${item.color}`}
            onClick={() => navigate(item.path)}
          >
            <div className="p-3 bg-white rounded-xl shadow-sm mr-4">{item.icon}</div>
            <div className="text-left">
              <h3 className="font-bold text-gray-800 text-lg">{item.title}</h3>
              <p className="text-gray-500 text-sm">{item.desc}</p>
            </div>
          </button>
        ))}
      </div>

      <div className="mt-10 p-4 border-t border-gray-100">
        <h4 className="text-xs font-bold text-gray-400 uppercase tracking-widest mb-3">Monitoring Number</h4>
        <div className="flex items-center text-gray-700 bg-gray-50 p-3 rounded-lg">
          <MapPin size={18} className="mr-2 text-red-400" />
          <span className="font-mono">9555769448</span>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;