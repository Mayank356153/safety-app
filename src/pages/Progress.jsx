import React from 'react';
import { CheckCircle2, Circle, Trophy, Star, Heart } from 'lucide-react';

const Progress = () => {
  // This would ideally come from your database
  const totalHelps = 3; 
  
  const milestones = [
    { id: 1, label: "First Responder", count: 1, desc: "Completed your first help" },
    { id: 2, label: "Safety Hero", count: 2, desc: "Twice the impact" },
    { id: 3, label: "Community Guardian", count: 3, desc: "A reliable protector" },
    { id: 4, label: "Lifesaver", count: 5, desc: "Making a massive difference" },
    { id: 5, label: "Guardian Angel", count: 10, desc: "The ultimate community hero" },
  ];

  return (
    <div className="min-h-screen bg-slate-50 pb-12">
      {/* Hero Stats Header */}
      <div className="bg-gradient-to-br from-blue-600 to-indigo-700 p-8 rounded-b-[40px] shadow-lg text-center text-white">
        <div className="bg-white/20 w-20 h-20 rounded-full flex items-center justify-center mx-auto mb-4 backdrop-blur-md">
          <Trophy size={40} className="text-yellow-300" />
        </div>
        <h1 className="text-3xl font-bold">{totalHelps} Helps</h1>
        <p className="text-blue-100 opacity-80">Your Contribution Score</p>
      </div>

      {/* The Achievement Path */}
      <div className="max-w-md mx-auto px-6 py-10">
        <h2 className="text-gray-400 text-xs font-bold uppercase tracking-[0.2em] mb-8">Your Impact Journey</h2>
        
        <div className="relative">
          {/* Vertical Line */}
          <div className="absolute left-4 top-0 bottom-0 w-0.5 bg-gray-200"></div>

          <div className="space-y-12">
            {milestones.map((step) => {
              const isCompleted = totalHelps >= step.count;
              const isNext = totalHelps + 1 === step.count;

              return (
                <div key={step.id} className="relative flex items-start ml-1">
                  {/* Icon/Circle */}
                  <div className={`z-10 flex items-center justify-center w-7 h-7 rounded-full border-4 ${
                    isCompleted 
                    ? "bg-green-500 border-green-100" 
                    : "bg-white border-gray-200"
                  }`}>
                    {isCompleted ? (
                      <CheckCircle2 size={14} className="text-white" />
                    ) : (
                      <Circle size={10} className={isNext ? "text-blue-500 fill-blue-500" : "text-gray-300"} />
                    )}
                  </div>

                  {/* Content */}
                  <div className="ml-6">
                    <h3 className={`font-bold ${isCompleted ? "text-gray-800" : "text-gray-400"}`}>
                      {step.label}
                    </h3>
                    <p className="text-sm text-gray-500">{step.desc}</p>
                    {isCompleted && (
                      <span className="inline-block mt-1 text-[10px] font-bold bg-green-100 text-green-700 px-2 py-0.5 rounded uppercase">
                        Achieved
                      </span>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </div>

      {/* Appreciation Quote Card */}
      <div className="px-6 mt-4">
        <div className="bg-white p-6 rounded-3xl shadow-sm border border-blue-50 relative overflow-hidden">
          <Heart className="absolute -right-4 -bottom-4 text-red-50 opacity-10" size={120} />
          <div className="relative z-10">
            <Star className="text-amber-400 mb-3" fill="currentColor" size={24} />
            <p className="text-gray-700 italic text-lg leading-relaxed">
              "Greatest act of a human being is to help another. Your quick response today might be someone's miracle tomorrow."
            </p>
            <div className="mt-4 flex items-center">
              <div className="h-1 w-12 bg-blue-500 rounded-full"></div>
              <span className="ml-3 text-xs font-bold text-blue-600 uppercase">Team Visionary Geeks</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Progress;