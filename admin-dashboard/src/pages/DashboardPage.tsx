import React, { useEffect, useState } from 'react';
import {
  Users,
  UserCheck,
  Calendar,
  DollarSign,
  FileCheck2,
  AlertTriangle,
  ArrowUpRight,
  TrendingUp,
} from 'lucide-react';
import {
  ResponsiveContainer,
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
} from 'recharts';
import { StatCard } from '../components/common/StatCard';
import { StatusBadge, StatusType } from '../components/common/StatusBadge';
import { Link } from 'react-router-dom';
import { adminApi, ApiBooking, ApiHelperProfile, ApiKycDocument, ApiUser, ApiDispute } from '../lib/adminApi';

function toStatusType(status: string): StatusType {
  if (status === 'completed' || status === 'cancelled') return status;
  if (status === 'requested') return 'pending';
  if (status === 'on_the_way' || status === 'in_progress') return 'in_progress';
  if (status === 'accepted') return 'active';
  return 'pending';
}

export const DashboardPage: React.FC = () => {
  const [users, setUsers] = useState<ApiUser[]>([]);
  const [helpers, setHelpers] = useState<ApiHelperProfile[]>([]);
  const [bookings, setBookings] = useState<ApiBooking[]>([]);
  const [kycDocs, setKycDocs] = useState<ApiKycDocument[]>([]);
  const [disputes, setDisputes] = useState<ApiDispute[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    (async () => {
      try {
        const [u, h, b, k, d] = await Promise.all([
          adminApi.getUsers(),
          adminApi.getHelpers(),
          adminApi.getBookings(),
          adminApi.getKycDocuments('pending'),
          adminApi.getDisputes(),
        ]);
        setUsers(u);
        setHelpers(h);
        setBookings(b);
        setKycDocs(k);
        setDisputes(d);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to load dashboard data.');
      } finally {
        setIsLoading(false);
      }
    })();
  }, []);

  const today = new Date().toDateString();
  const bookingsToday = bookings.filter((b) => new Date(b.created_at).toDateString() === today);
  const revenueToday = bookingsToday
    .filter((b) => b.status === 'completed')
    .reduce((sum, b) => sum + Number(b.total_amount), 0);
  const openDisputes = disputes.filter((d) => d.status !== 'resolved');
  const activeHelpers = helpers.filter((h) => h.kyc_status === 'approved');
  const recentBookings = [...bookings]
    .sort((a, b) => new Date(b.created_at).getTime() - new Date(a.created_at).getTime())
    .slice(0, 5);

  // Last 7 days, bucketed from real bookings — no fabricated numbers.
  const trendData = Array.from({ length: 7 }).map((_, i) => {
    const date = new Date();
    date.setDate(date.getDate() - (6 - i));
    const dayLabel = date.toLocaleDateString(undefined, { weekday: 'short' });
    const dayBookings = bookings.filter((b) => new Date(b.created_at).toDateString() === date.toDateString());
    const revenue = dayBookings.filter((b) => b.status === 'completed').reduce((sum, b) => sum + Number(b.total_amount), 0);
    return { day: dayLabel, bookings: dayBookings.length, revenue };
  });

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Dashboard Overview</h1>
          <p className="text-xs text-slate-500 mt-1">Live status of the 1-Hour Services platform.</p>
        </div>
        <Link
          to="/bookings"
          className="flex items-center gap-2 px-3.5 py-2 bg-lumina-primary text-white rounded-lg text-xs font-semibold hover:bg-teal-700 shadow-sm transition-colors"
        >
          <span>View All Bookings</span>
          <ArrowUpRight className="w-4 h-4" />
        </Link>
      </div>

      {error && <div className="bg-rose-50 border border-rose-200 text-rose-700 text-xs px-4 py-3 rounded-lg">{error}</div>}

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6 gap-4">
        <StatCard title="Total Users" value={isLoading ? '—' : users.length} icon={Users} iconBgColor="bg-teal-50" iconTextColor="text-lumina-primary" />
        <StatCard title="Approved Helpers" value={isLoading ? '—' : activeHelpers.length} icon={UserCheck} iconBgColor="bg-emerald-50" iconTextColor="text-emerald-600" />
        <StatCard title="Bookings Today" value={isLoading ? '—' : bookingsToday.length} icon={Calendar} iconBgColor="bg-blue-50" iconTextColor="text-blue-600" />
        <StatCard title="Revenue Today" value={isLoading ? '—' : `₹${revenueToday.toFixed(2)}`} icon={DollarSign} iconBgColor="bg-indigo-50" iconTextColor="text-indigo-600" />
        <StatCard
          title="Pending KYC"
          value={isLoading ? '—' : kycDocs.length}
          badge={kycDocs.length > 0 ? 'Requires Action' : undefined}
          badgeColor="bg-amber-100 text-amber-800 border border-amber-200"
          icon={FileCheck2}
          iconBgColor="bg-amber-50"
          iconTextColor="text-amber-600"
        />
        <StatCard
          title="Open Complaints"
          value={isLoading ? '—' : openDisputes.length}
          badge={openDisputes.length > 0 ? 'High Priority' : undefined}
          badgeColor="bg-rose-100 text-rose-800 border border-rose-200"
          icon={AlertTriangle}
          iconBgColor="bg-rose-50"
          iconTextColor="text-rose-600"
        />
      </div>

      <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between pb-6 border-b border-slate-100 gap-2">
          <div>
            <div className="flex items-center gap-2">
              <TrendingUp className="w-5 h-5 text-lumina-primary" />
              <h2 className="text-base font-bold text-slate-900">7-Day Bookings & Revenue Trend</h2>
            </div>
            <p className="text-xs text-slate-500 mt-0.5">Computed from real bookings created in the last 7 days</p>
          </div>
        </div>

        <div className="h-72 mt-6">
          <ResponsiveContainer width="100%" height="100%">
            <AreaChart data={trendData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
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
              <Area type="monotone" dataKey="revenue" stroke="#009488" strokeWidth={2.5} fillOpacity={1} fill="url(#revenueGrad)" name="Revenue (₹)" isAnimationActive={false} />
              <Area type="monotone" dataKey="bookings" stroke="#94a3b8" strokeWidth={2} fillOpacity={1} fill="url(#bookingsGrad)" name="Bookings" isAnimationActive={false} />
            </AreaChart>
          </ResponsiveContainer>
        </div>
      </div>

      <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="p-5 border-b border-slate-100 flex items-center justify-between">
          <div>
            <h2 className="text-base font-bold text-slate-900">Recent Bookings</h2>
            <p className="text-xs text-slate-500 mt-0.5">Latest service requests placed by customers</p>
          </div>
          <Link to="/bookings" className="text-xs font-semibold text-lumina-primary hover:underline flex items-center gap-1">
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
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {isLoading ? (
                <tr><td colSpan={6} className="py-8 text-center text-slate-400">Loading...</td></tr>
              ) : recentBookings.length === 0 ? (
                <tr><td colSpan={6} className="py-8 text-center text-slate-400">No bookings yet.</td></tr>
              ) : (
                recentBookings.map((b) => (
                  <tr key={b.id} className="hover:bg-slate-50/80 transition-colors">
                    <td className="py-3.5 px-5 font-bold text-slate-900">#{b.id}</td>
                    <td className="py-3.5 px-5 font-medium text-slate-800">{b.customer?.name ?? '—'}</td>
                    <td className="py-3.5 px-5 text-slate-600">{b.helper?.user?.name ?? '—'}</td>
                    <td className="py-3.5 px-5">
                      <span className="px-2 py-0.5 rounded bg-slate-100 font-medium text-slate-700 text-[11px]">
                        {b.category?.name ?? '—'}
                      </span>
                    </td>
                    <td className="py-3.5 px-5 font-semibold text-slate-900">₹{Number(b.total_amount).toFixed(2)}</td>
                    <td className="py-3.5 px-5"><StatusBadge status={toStatusType(b.status)} label={b.status.replace('_', ' ').toUpperCase()} /></td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
