import React, { useEffect, useState } from 'react';
import { RefreshCw } from 'lucide-react';
import { adminApi, ApiDispute } from '../lib/adminApi';
import { StatusBadge } from '../components/common/StatusBadge';

export const ComplaintsPage: React.FC = () => {
  const [disputes, setDisputes] = useState<ApiDispute[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = async () => {
    setIsLoading(true);
    setError(null);
    try {
      setDisputes(await adminApi.getDisputes());
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load complaints.');
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
          <h1 className="text-xl font-bold text-slate-900">Complaints / Disputes</h1>
          <p className="text-xs text-slate-500 mt-1">Customer-helper dispute tickets raised on bookings.</p>
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
                <th className="py-3.5 px-5">Booking</th>
                <th className="py-3.5 px-5">Description</th>
                <th className="py-3.5 px-5">Raised</th>
                <th className="py-3.5 px-5">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {isLoading ? (
                <tr><td colSpan={4} className="py-8 text-center text-slate-400">Loading...</td></tr>
              ) : disputes.length === 0 ? (
                <tr><td colSpan={4} className="py-8 text-center text-slate-400">No open complaints. 🎉</td></tr>
              ) : (
                disputes.map((d) => (
                  <tr key={d.id} className="hover:bg-slate-50/80">
                    <td className="py-3.5 px-5 font-bold text-slate-900">#{d.booking_id}</td>
                    <td className="py-3.5 px-5 max-w-md truncate">{d.description}</td>
                    <td className="py-3.5 px-5">{new Date(d.created_at).toLocaleDateString()}</td>
                    <td className="py-3.5 px-5"><StatusBadge status={d.status === 'resolved' ? 'completed' : d.status === 'open' ? 'open' : 'investigating'} label={d.status.replace('_', ' ').toUpperCase()} /></td>
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
