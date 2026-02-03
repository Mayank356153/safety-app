import React from "react";
import { api } from "../api/api";
import { MapPin, Navigation, User, Clock } from "lucide-react";

export default function AlertCard({ alert }) {

  const accept = async () => {
    try {
      await api.post("/help/accept", {
        helpId: alert.id,
        helperId: "helper-001",
      });

      console.log("✅ Help accepted");
    } catch (err) {
      console.error("❌ Accept failed", err);
    }
  };

  const openMaps = () => {
    window.open(
      `https://www.google.com/maps?q=${alert.lat},${alert.lon}`,
      "_blank"
    );
  };

  return (
    <div className="bg-slate-900 border border-slate-700 rounded-2xl overflow-hidden shadow-2xl mb-5">

      {/* MAP PREVIEW */}
      <div className="relative h-40 bg-slate-800 flex items-center justify-center">
        <MapPin className="text-red-500 animate-bounce" size={36} />

        <div className="absolute bottom-3 left-3 bg-red-600 text-white text-xs px-3 py-1 rounded-full flex items-center">
          <Navigation size={12} className="mr-1" />
          {alert.distance} km away
        </div>
      </div>

      {/* CONTENT */}
      <div className="p-5">
        <h3 className="text-white font-bold text-lg flex items-center">
          <User size={18} className="mr-2 text-blue-400" />
          Emergency Assistance
        </h3>

        <p className="text-slate-400 text-sm mt-1 flex items-center">
          <Clock size={14} className="mr-1" />
          {alert.time}
        </p>

        <div className="flex gap-3 mt-4">
          <button
            onClick={accept}
            className="flex-1 bg-green-600 hover:bg-green-500 text-white font-bold py-3 rounded-xl"
          >
            Accept
          </button>

          <button
            onClick={openMaps}
            className="flex-1 bg-blue-600 hover:bg-blue-500 text-white font-bold py-3 rounded-xl"
          >
            Navigate
          </button>
        </div>
      </div>
    </div>
  );
}
