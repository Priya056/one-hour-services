import React, { useEffect, useState } from 'react';
import { RefreshCw } from 'lucide-react';
import { adminApi, ApiBooking } from '../lib/adminApi';
import { StatusBadge, StatusType } from '../components/common/StatusBadge';

function toStatusType(status: string): StatusType {
  if (status === 'completed' || status === 'cancelled') return status;
  if (status === 'requested') return 'pending';
  if (status === 'on_the_way' || status === 'in_progress') return 'in_progress';
  if (status === 'accepted') return 'active';
  return 'pending';
}

export const BookingsPage: React.FC = () => {
  const [bookings, setBookings] = useState<ApiBooking[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await adminApi.getBookings();
      setBookings(data.sort((a, b) => new Date(b.created_at).getTime() - new Date(a.created_at).getTime()));
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load bookings.');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold text-slate-900">Bookings Management</h1>
          <p className="text-xs text-slate-500 mt-1">All 1-hour service bookings across the platform.</p>
        </div>
        <button
          onClick={load}
          className="flex items-center gap-2 px-3 py-2 bg-white border border-slate-200 rounded-lg text-xs font-semibold text-slate-700 hover:bg-slate-50"
        >
          <RefreshCw className="w-3.5 h-3.5" /> Refresh
        </button>
      </div>

      {error && <div className="bg-rose-50 border border-rose-200 text-rose-700 text-xs px-4 py-3 rounded-lg">{error}</div>}

      <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-700">
            <thead className="bg-slate-50 text-slate-500 font-semibold border-b border-slate-200 uppercase tracking-wider text-[11px]">
              <tr>
                <th className="py-3.5 px-5">Booking ID</th>
                <th className="py-3.5 px-5">Customer</th>
                <th className="py-3.5 px-5">Helper</th>
                <th className="py-3.5 px-5">Category</th>
                <th className="py-3.5 px-5">Amount</th>
                <th className="py-3.5 px-5">Scheduled</th>
                <th className="py-3.5 px-5">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {isLoading ? (
                <tr><td colSpan={7} className="py-8 text-center text-slate-400">Loading...</td></tr>
              ) : bookings.length === 0 ? (
                <tr><td colSpan={7} className="py-8 text-center text-slate-400">No bookings yet.</td></tr>
              ) : (
                bookings.map((b) => (
                  <tr key={b.id} className="hover:bg-slate-50/80">
                    <td className="py-3.5 px-5 font-bold text-slate-900">#{b.id}</td>
                    <td className="py-3.5 px-5">{b.customer?.name ?? '—'}</td>
                    <td className="py-3.5 px-5">{b.helper?.user?.name ?? '—'}</td>
                    <td className="py-3.5 px-5">
                      <span className="px-2 py-0.5 rounded bg-slate-100 font-medium text-slate-700 text-[11px]">
                        {b.category?.name ?? '—'}
                      </span>
                    </td>
                    <td className="py-3.5 px-5 font-semibold text-slate-900">₹{Number(b.total_amount).toFixed(2)}</td>
                    <td className="py-3.5 px-5">{new Date(b.scheduled_time).toLocaleString()}</td>
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
