import React, { useEffect, useState } from 'react';
import { CheckCircle2, XCircle, RefreshCw } from 'lucide-react';
import { adminApi, ApiHelperProfile } from '../lib/adminApi';
import { StatusBadge } from '../components/common/StatusBadge';

export const HelpersPage: React.FC = () => {
  const [helpers, setHelpers] = useState<ApiHelperProfile[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [actioningId, setActioningId] = useState<number | null>(null);

  const load = async () => {
    setIsLoading(true);
    setError(null);
    try {
      setHelpers(await adminApi.getHelpers());
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load helpers.');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const handleAction = async (id: number, action: 'approve' | 'reject') => {
    setActioningId(id);
    try {
      if (action === 'approve') await adminApi.approveHelper(id);
      else await adminApi.rejectHelper(id);
      await load();
    } catch (err: any) {
      setError(err.response?.data?.message || `Failed to ${action} helper.`);
    } finally {
      setActioningId(null);
    }
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold text-slate-900">Helpers Management</h1>
          <p className="text-xs text-slate-500 mt-1">Manage verified service helpers and their KYC status.</p>
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
                <th className="py-3.5 px-5">Name</th>
                <th className="py-3.5 px-5">Phone</th>
                <th className="py-3.5 px-5">Rating</th>
                <th className="py-3.5 px-5">Reviews</th>
                <th className="py-3.5 px-5">Available Now</th>
                <th className="py-3.5 px-5">KYC Status</th>
                <th className="py-3.5 px-5 text-right">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {isLoading ? (
                <tr><td colSpan={7} className="py-8 text-center text-slate-400">Loading...</td></tr>
              ) : helpers.length === 0 ? (
                <tr><td colSpan={7} className="py-8 text-center text-slate-400">No helpers yet.</td></tr>
              ) : (
                helpers.map((h) => (
                  <tr key={h.id} className="hover:bg-slate-50/80">
                    <td className="py-3.5 px-5 font-semibold text-slate-900">{h.user?.name ?? '—'}</td>
                    <td className="py-3.5 px-5">{h.user?.phone ?? '—'}</td>
                    <td className="py-3.5 px-5">{h.average_rating ? Number(h.average_rating).toFixed(1) : '—'}</td>
                    <td className="py-3.5 px-5">{h.total_reviews ?? 0}</td>
                    <td className="py-3.5 px-5">{h.is_available_now ? 'Yes' : 'No'}</td>
                    <td className="py-3.5 px-5">
                      <StatusBadge status={h.kyc_status === 'approved' ? 'approved' : h.kyc_status === 'rejected' ? 'rejected' : 'pending'} />
                    </td>
                    <td className="py-3.5 px-5 text-right space-x-2">
                      {h.kyc_status !== 'approved' && (
                        <button
                          onClick={() => handleAction(h.id, 'approve')}
                          disabled={actioningId === h.id}
                          className="inline-flex items-center gap-1 px-2.5 py-1 rounded-md bg-emerald-50 text-emerald-700 font-semibold hover:bg-emerald-100 disabled:opacity-50"
                        >
                          <CheckCircle2 className="w-3.5 h-3.5" /> Approve
                        </button>
                      )}
                      {h.kyc_status !== 'rejected' && (
                        <button
                          onClick={() => handleAction(h.id, 'reject')}
                          disabled={actioningId === h.id}
                          className="inline-flex items-center gap-1 px-2.5 py-1 rounded-md bg-rose-50 text-rose-700 font-semibold hover:bg-rose-100 disabled:opacity-50"
                        >
                          <XCircle className="w-3.5 h-3.5" /> Reject
                        </button>
                      )}
                    </td>
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
