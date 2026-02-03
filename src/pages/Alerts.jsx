import React from "react";
import AlertCard from "../components/AlertCard";

export default function Alerts() {
  const mockAlert = {
    id: "help-001",
    distance: 1.2,
    lat: "28.6139",
    lon: "77.2090",
    time: "Just now"
  };

  return (
    <div className="min-h-screen bg-black p-4">
      <h1 className="text-white text-xl font-bold mb-4">
        🚨 Emergency Alerts
      </h1>

      <AlertCard alert={mockAlert} />
    </div>
  );
}
