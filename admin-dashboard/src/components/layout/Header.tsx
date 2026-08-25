import React, { useState } from 'react';
import { Search, Bell, ChevronDown, User, Shield, LogOut, Settings, CheckCircle2 } from 'lucide-react';

export const Header: React.FC = () => {
  const [showNotifications, setShowNotifications] = useState(false);
  const [showProfileMenu, setShowProfileMenu] = useState(false);

  const notifications = [
    { id: 1, title: 'New KYC Submission', time: '10 mins ago', desc: 'Helper Rajesh M. uploaded driving license.', unread: true },
    { id: 2, title: 'New Dispute Escalated', time: '45 mins ago', desc: 'Booking #LUM-9402 reported incomplete job.', unread: true },
    { id: 3, title: 'High Volume Warning', time: '2 hours ago', desc: 'Electrician category reached 90% helper utilization.', unread: false },
  ];

  return (
    <header className="h-16 bg-white border-b border-slate-200 fixed top-0 right-0 left-64 z-20 px-8 flex items-center justify-between shadow-sm">
      {/* Global Search Bar */}
      <div className="relative w-96">
        <Search className="w-4 h-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
        <input
          type="text"
          placeholder="Search bookings, users, helpers, KYC docs..."
          className="w-full pl-10 pr-4 py-2 bg-slate-50 border border-slate-200 rounded-lg text-xs text-slate-800 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-lumina-primary/30 focus:border-lumina-primary transition-all"
        />
      </div>

      {/* Right Controls */}
      <div className="flex items-center gap-5">
        {/* Environment Badge */}
        <div className="hidden md:flex items-center gap-1.5 px-2.5 py-1 bg-teal-50 border border-teal-200 rounded-full text-[11px] font-semibold text-lumina-primary">
          <span className="w-1.5 h-1.5 rounded-full bg-lumina-primary"></span>
          <span>Mock API Active</span>
        </div>

        {/* Notifications Dropdown */}
        <div className="relative">
          <button
            onClick={() => setShowNotifications(!showNotifications)}
            className="p-2 rounded-lg text-slate-500 hover:text-slate-800 hover:bg-slate-100 transition-colors relative"
            aria-label="Notifications"
          >
            <Bell className="w-5 h-5" />
            <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-rose-500 rounded-full ring-2 ring-white"></span>
          </button>

          {showNotifications && (
            <div className="absolute right-0 mt-2 w-80 bg-white rounded-xl shadow-xl border border-slate-100 py-2 z-50 animate-in fade-in duration-150">
              <div className="px-4 py-2 border-b border-slate-100 flex items-center justify-between">
                <span className="text-xs font-bold text-slate-800">Notifications</span>
                <span className="text-[10px] font-medium text-lumina-primary hover:underline cursor-pointer">Mark all read</span>
              </div>
              <div className="max-h-64 overflow-y-auto divide-y divide-slate-50">
                {notifications.map((n) => (
                  <div key={n.id} className={`p-3 text-xs hover:bg-slate-50 cursor-pointer ${n.unread ? 'bg-teal-50/30' : ''}`}>
                    <div className="flex items-center justify-between font-semibold text-slate-800">
                      <span>{n.title}</span>
                      <span className="text-[10px] text-slate-400 font-normal">{n.time}</span>
                    </div>
                    <p className="text-[11px] text-slate-500 mt-1">{n.desc}</p>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* Divider */}
        <div className="h-6 w-px bg-slate-200"></div>

        {/* Admin Profile Dropdown */}
        <div className="relative">
          <button
            onClick={() => setShowProfileMenu(!showProfileMenu)}
            className="flex items-center gap-3 p-1.5 rounded-lg hover:bg-slate-100 transition-colors"
          >
            <div className="w-8 h-8 rounded-full bg-slate-900 text-white font-bold text-xs flex items-center justify-center border border-slate-200 shadow-sm">
              AD
            </div>
            <div className="text-left hidden sm:block">
              <div className="text-xs font-bold text-slate-800 leading-tight">Alex Rivera</div>
              <div className="text-[10px] text-teal-600 font-medium flex items-center gap-1">
                <Shield className="w-3 h-3 text-lumina-primary" />
                <span>Super Admin</span>
              </div>
            </div>
            <ChevronDown className="w-4 h-4 text-slate-400" />
          </button>

          {showProfileMenu && (
            <div className="absolute right-0 mt-2 w-48 bg-white rounded-xl shadow-xl border border-slate-100 py-1 z-50">
              <div className="px-4 py-2 border-b border-slate-100 sm:hidden">
                <p className="text-xs font-bold text-slate-800">Alex Rivera</p>
                <p className="text-[10px] text-teal-600">Super Admin</p>
              </div>
              <a href="#profile" className="flex items-center gap-2.5 px-4 py-2 text-xs text-slate-700 hover:bg-slate-50">
                <User className="w-4 h-4 text-slate-400" />
                <span>Admin Profile</span>
              </a>
              <a href="#settings" className="flex items-center gap-2.5 px-4 py-2 text-xs text-slate-700 hover:bg-slate-50">
                <Settings className="w-4 h-4 text-slate-400" />
                <span>System Settings</span>
              </a>
              <div className="border-t border-slate-100 my-1"></div>
              <button className="w-full flex items-center gap-2.5 px-4 py-2 text-xs text-rose-600 hover:bg-rose-50 text-left font-medium">
                <LogOut className="w-4 h-4" />
                <span>Sign Out</span>
              </button>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};
