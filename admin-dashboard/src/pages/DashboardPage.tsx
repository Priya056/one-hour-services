import React from 'react';
import { 
  Users, 
  UserCheck, 
  Calendar, 
  DollarSign, 
  FileCheck2, 
  AlertTriangle,
  ArrowUpRight,
  TrendingUp,
  Download
} from 'lucide-react';
import { 
  ResponsiveContainer, 
  AreaChart, 
  Area, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  Legend 
} from 'recharts';
import { StatCard } from '../components/common/StatCard';
import { StatusBadge } from '../components/common/StatusBadge';
import { mockTrendData, mockRecentBookings } from '../mock-data/dashboardMock';
import { Link } from 'react-router-dom';

export const DashboardPage: React.FC = () => {
  return (
    <div className="space-y-6">
      {/* Top Banner / Welcome */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Dashboard Overview</h1>
          <p className="text-xs text-slate-500 mt-1">Real-time status of Lumina 1-hour services platform.</p>
        </div>
        <div className="flex items-center gap-3">
          <button className="flex items-center gap-2 px-3.5 py-2 bg-white border border-slate-200 rounded-lg text-xs font-semibold text-slate-700 hover:bg-slate-50 shadow-2xs transition-colors">
            <Download className="w-4 h-4 text-slate-400" />
            <span>Export Report</span>
          </button>
          <Link
            to="/bookings"
            className="flex items-center gap-2 px-3.5 py-2 bg-lumina-primary text-white rounded-lg text-xs font-semibold hover:bg-teal-700 shadow-sm transition-colors"
          >
            <span>View All Bookings</span>
            <ArrowUpRight className="w-4 h-4" />
          </Link>
        </div>
      </div>

      {/* 6 Stat Cards Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6 gap-4">
        <StatCard
          title="Total Users"
          value="2,840"
          change="12%"
          isPositive={true}
          icon={Users}
          iconBgColor="bg-teal-50"
          iconTextColor="text-lumina-primary"
        />
        <StatCard
          title="Active Helpers"
          value="342"
          change="8%"
          isPositive={true}
          icon={UserCheck}
          iconBgColor="bg-emerald-50"
          iconTextColor="text-emerald-600"
        />
        <StatCard
          title="Bookings Today"
          value="128"
          change="15%"
          isPositive={true}
          icon={Calendar}
          iconBgColor="bg-blue-50"
          iconTextColor="text-blue-600"
        />
        <StatCard
          title="Revenue Today"
          value="$3,450.00"
          change="18%"
          isPositive={true}
          icon={DollarSign}
          iconBgColor="bg-indigo-50"
          iconTextColor="text-indigo-600"
        />
        <StatCard
          title="Pending KYC"
          value="5"
          badge="Requires Action"
          badgeColor="bg-amber-100 text-amber-800 border border-amber-200"
          icon={FileCheck2}
          iconBgColor="bg-amber-50"
          iconTextColor="text-amber-600"
        />
        <StatCard
          title="Open Complaints"
          value="3"
          badge="High Priority"
          badgeColor="bg-rose-100 text-rose-800 border border-rose-200"
          icon={AlertTriangle}
          iconBgColor="bg-rose-50"
          iconTextColor="text-rose-600"
        />
      </div>

      {/* 7-Day Trend Graph & Analytics Card */}
      <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between pb-6 border-b border-slate-100 gap-2">
          <div>
            <div className="flex items-center gap-2">
              <TrendingUp className="w-5 h-5 text-lumina-primary" />
              <h2 className="text-base font-bold text-slate-900">7-Day Bookings & Revenue Trend</h2>
            </div>
            <p className="text-xs text-slate-500 mt-0.5">Daily performance metrics across all 1-hour service categories</p>
          </div>
          <div className="flex items-center gap-4 text-xs font-semibold">
            <span className="flex items-center gap-1.5 text-lumina-primary">
              <span className="w-3 h-3 rounded-sm bg-lumina-primary inline-block"></span> Revenue ($)
            </span>
            <span className="flex items-center gap-1.5 text-slate-400">
              <span className="w-3 h-3 rounded-sm bg-slate-300 inline-block"></span> Bookings Count
            </span>
          </div>
        </div>

        <div className="h-72 mt-6">
          <ResponsiveContainer width="100%" height="100%">
            <AreaChart data={mockTrendData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
              <defs>
                <linearGradient id="revenueGrad" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#009488" stopOpacity={0.3} />
                  <stop offset="95%" stopColor="#009488" stopOpacity={0.0} />
                </linearGradient>
                <linearGradient id="bookingsGrad" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#94a3b8" stopOpacity={0.2} />
                  <stop offset="95%" stopColor="#94a3b8" stopOpacity={0.0} />
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f1f5f9" />
              <XAxis dataKey="day" axisLine={false} tickLine={false} tick={{ fill: '#64748b', fontSize: 12 }} />
              <YAxis axisLine={false} tickLine={false} tick={{ fill: '#64748b', fontSize: 12 }} />
              <Tooltip 
                contentStyle={{ backgroundColor: '#0f172a', borderRadius: '8px', border: 'none', color: '#fff', fontSize: '12px' }}
                itemStyle={{ color: '#5eead4' }}
              />
              <Area type="monotone" dataKey="revenue" stroke="#009488" strokeWidth={2.5} fillOpacity={1} fill="url(#revenueGrad)" name="Revenue ($)" isAnimationActive={false} />
              <Area type="monotone" dataKey="bookings" stroke="#94a3b8" strokeWidth={2} fillOpacity={1} fill="url(#bookingsGrad)" name="Bookings" isAnimationActive={false} />
            </AreaChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Recent Bookings Table */}
      <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="p-5 border-b border-slate-100 flex items-center justify-between">
          <div>
            <h2 className="text-base font-bold text-slate-900">Recent Bookings</h2>
            <p className="text-xs text-slate-500 mt-0.5">Latest service requests placed by customers</p>
          </div>
          <Link
            to="/bookings"
            className="text-xs font-semibold text-lumina-primary hover:underline flex items-center gap-1"
          >
            View all bookings &rarr;
          </Link>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-700">
            <thead className="bg-slate-50 text-slate-500 font-semibold border-b border-slate-200 uppercase tracking-wider text-[11px]">
              <tr>
                <th className="py-3.5 px-5">Booking ID</th>
                <th className="py-3.5 px-5">Customer</th>
                <th className="py-3.5 px-5">Helper</th>
                <th className="py-3.5 px-5">Category</th>
                <th className="py-3.5 px-5">Amount</th>
                <th className="py-3.5 px-5">Status</th>
                <th className="py-3.5 px-5 text-right">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {mockRecentBookings.map((b) => (
                <tr key={b.id} className="hover:bg-slate-50/80 transition-colors">
                  <td className="py-3.5 px-5 font-bold text-slate-900">{b.bookingNumber}</td>
                  <td className="py-3.5 px-5 font-medium text-slate-800">{b.customerName}</td>
                  <td className="py-3.5 px-5 text-slate-600">{b.helperName}</td>
                  <td className="py-3.5 px-5">
                    <span className="px-2 py-0.5 rounded bg-slate-100 font-medium text-slate-700 text-[11px]">
                      {b.category}
                    </span>
                  </td>
                  <td className="py-3.5 px-5 font-semibold text-slate-900">${b.amount.toFixed(2)}</td>
                  <td className="py-3.5 px-5">
                    <StatusBadge status={b.status} />
                  </td>
                  <td className="py-3.5 px-5 text-right">
                    <Link
                      to="/bookings"
                      className="text-xs font-semibold text-lumina-primary hover:text-teal-800 hover:underline"
                    >
                      Details
                    </Link>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
