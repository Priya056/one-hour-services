import React from 'react';
import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard,
  Users,
  UserCheck,
  FileCheck2,
  Grid,
  Calendar,
  CreditCard,
  Percent,
  RotateCcw,
  AlertTriangle,
  Star,
  BarChart3,
  MapPin,
  Clock,
  ChevronRight
} from 'lucide-react';

interface SidebarProps {
  pendingKycCount?: number;
  openComplaintsCount?: number;
}

export const Sidebar: React.FC<SidebarProps> = ({
  pendingKycCount = 5,
  openComplaintsCount = 3,
}) => {
  const navItems = [
    { label: 'Dashboard', path: '/', icon: LayoutDashboard },
    { label: 'Users', path: '/users', icon: Users },
    { label: 'Helpers', path: '/helpers', icon: UserCheck },
    { 
      label: 'KYC Approvals', 
      path: '/kyc-approvals', 
      icon: FileCheck2, 
      badge: pendingKycCount > 0 ? pendingKycCount : undefined,
      badgeColor: 'bg-amber-500 text-slate-950 font-bold'
    },
    { label: 'Categories', path: '/categories', icon: Grid },
    { label: 'Bookings', path: '/bookings', icon: Calendar },
    { label: 'Payments', path: '/payments', icon: CreditCard },
    { label: 'Commission Settings', path: '/commission-settings', icon: Percent },
    { label: 'Refunds', path: '/refunds', icon: RotateCcw },
    { 
      label: 'Complaints/Disputes', 
      path: '/complaints', 
      icon: AlertTriangle,
      badge: openComplaintsCount > 0 ? openComplaintsCount : undefined,
      badgeColor: 'bg-rose-500 text-white'
    },
    { label: 'Reviews', path: '/reviews', icon: Star },
    { label: 'Reports & Analytics', path: '/reports', icon: BarChart3 },
    { label: 'Locations/Service Areas', path: '/locations', icon: MapPin },
  ];

  return (
    <aside className="w-64 bg-lumina-secondary text-slate-300 flex flex-col fixed inset-y-0 left-0 z-30 shadow-xl border-r border-slate-800">
      {/* Brand Header */}
      <div className="h-16 flex items-center px-6 border-b border-slate-800 bg-slate-950/40">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-lumina-primary to-teal-400 flex items-center justify-center text-white font-bold shadow-md shadow-teal-900/30">
            <Clock className="w-5 h-5 text-white" />
          </div>
          <div>
            <h1 className="font-bold text-white tracking-wide text-lg leading-tight">LUMINA</h1>
            <p className="text-[10px] text-teal-400 font-medium uppercase tracking-wider">1-Hour Admin Hub</p>
          </div>
        </div>
      </div>

      {/* Navigation List */}
      <div className="flex-1 overflow-y-auto px-3 py-4 space-y-1">
        <div className="px-3 pb-2 text-[11px] font-semibold tracking-wider text-slate-500 uppercase">
          Main Menu
        </div>
        {navItems.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) =>
                `flex items-center justify-between px-3.5 py-2.5 rounded-lg text-xs font-medium transition-all duration-150 ${
                  isActive
                    ? 'bg-lumina-primary text-white shadow-md shadow-teal-900/20 font-semibold'
                    : 'text-slate-400 hover:bg-slate-800/80 hover:text-slate-200'
                }`
              }
            >
              {({ isActive }) => (
                <>
                  <div className="flex items-center gap-3 min-w-0">
                    <Icon className={`w-4 h-4 flex-shrink-0 ${isActive ? 'text-white' : 'text-slate-400'}`} />
                    <span className="truncate">{item.label}</span>
                  </div>
                  <div className="flex items-center gap-1.5">
                    {item.badge !== undefined && (
                      <span className={`px-1.5 py-0.5 rounded-full text-[10px] ${item.badgeColor}`}>
                        {item.badge}
                      </span>
                    )}
                    {isActive && <ChevronRight className="w-3.5 h-3.5 opacity-80" />}
                  </div>
                </>
              )}
            </NavLink>
          );
        })}
      </div>

      {/* Footer Info */}
      <div className="p-4 border-t border-slate-800 bg-slate-950/30 text-[11px] text-slate-500 flex items-center justify-between">
        <span>v1.0.0 Stable</span>
        <span className="inline-flex items-center gap-1.5 text-teal-400 font-medium">
          <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span> API Ready
        </span>
      </div>
    </aside>
  );
};
