import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, ChevronDown, User, Shield, LogOut } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

export const Header: React.FC = () => {
  const [showProfileMenu, setShowProfileMenu] = useState(false);
  const { admin, logout } = useAuth();
  const navigate = useNavigate();

  const initials = admin?.name
    ? admin.name.split(' ').map((p) => p[0]).slice(0, 2).join('').toUpperCase()
    : 'AD';

  const handleSignOut = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="h-16 bg-white border-b border-slate-200 fixed top-0 right-0 left-64 z-20 px-8 flex items-center justify-between shadow-sm">
      {/* Global Search Bar */}
      <div className="relative w-96">
        <Search className="w-4 h-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
        <input
          type="text"
          placeholder="Search bookings, users, helpers, KYC docs..."
          className="w-full pl-10 pr-4 py-2 bg-slate-50 border border-slate-200 rounded-lg text-xs text-slate-800 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-lumina-primary/30 focus:border-lumina-primary transition-all"
          disabled
          title="Global search not built yet"
        />
      </div>

      {/* Right Controls */}
      <div className="flex items-center gap-5">
        {/* Admin Profile Dropdown */}
        <div className="relative">
          <button
            onClick={() => setShowProfileMenu(!showProfileMenu)}
            className="flex items-center gap-3 p-1.5 rounded-lg hover:bg-slate-100 transition-colors"
          >
            <div className="w-8 h-8 rounded-full bg-slate-900 text-white font-bold text-xs flex items-center justify-center border border-slate-200 shadow-sm">
              {initials}
            </div>
            <div className="text-left hidden sm:block">
              <div className="text-xs font-bold text-slate-800 leading-tight">{admin?.name ?? 'Admin'}</div>
              <div className="text-[10px] text-teal-600 font-medium flex items-center gap-1">
                <Shield className="w-3 h-3 text-lumina-primary" />
                <span>Admin</span>
              </div>
            </div>
            <ChevronDown className="w-4 h-4 text-slate-400" />
          </button>

          {showProfileMenu && (
            <div className="absolute right-0 mt-2 w-48 bg-white rounded-xl shadow-xl border border-slate-100 py-1 z-50">
              <div className="px-4 py-2 border-b border-slate-100 sm:hidden">
                <p className="text-xs font-bold text-slate-800">{admin?.name ?? 'Admin'}</p>
                <p className="text-[10px] text-teal-600">Admin</p>
              </div>
              <div className="flex items-center gap-2.5 px-4 py-2 text-xs text-slate-500">
                <User className="w-4 h-4 text-slate-400" />
                <span>{admin?.phone ?? '—'}</span>
              </div>
              <div className="border-t border-slate-100 my-1"></div>
              <button
                onClick={handleSignOut}
                className="w-full flex items-center gap-2.5 px-4 py-2 text-xs text-rose-600 hover:bg-rose-50 text-left font-medium"
              >
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
