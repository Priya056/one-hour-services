import React, { useEffect, useState } from 'react';
import { CheckCircle2, XCircle, RefreshCw, ExternalLink } from 'lucide-react';
import { adminApi, ApiKycDocument } from '../lib/adminApi';
import { StatusBadge } from '../components/common/StatusBadge';

type FilterTab = 'pending' | 'approved' | 'rejected' | 'all';

export const KycApprovalsPage: React.FC = () => {
  const [documents, setDocuments] = useState<ApiKycDocument[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [actioningId, setActioningId] = useState<number | null>(null);
  const [tab, setTab] = useState<FilterTab>('pending');

  const load = async () => {
    setIsLoading(true);
    setError(null);
    try {
      setDocuments(await adminApi.getKycDocuments(tab === 'all' ? undefined : tab));
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load KYC documents.');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [tab]);

  const handleAction = async (id: number, action: 'approve' | 'reject') => {
    setActioningId(id);
    try {
      if (action === 'approve') await adminApi.approveKyc(id);
      else await adminApi.rejectKyc(id);
      await load();
    } catch (err: any) {
      setError(err.response?.data?.message || `Failed to ${action} document.`);
    } finally {
      setActioningId(null);
    }
  };

  const tabs: { key: FilterTab; label: string }[] = [
    { key: 'pending', label: 'Pending' },
    { key: 'approved', label: 'Approved' },
    { key: 'rejected', label: 'Rejected' },
    { key: 'all', label: 'All' },
  ];

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold text-slate-900">KYC Approvals</h1>
          <p className="text-xs text-slate-500 mt-1">Review and approve helper identity documents.</p>
        </div>
        <button
          onClick={load}
          className="flex items-center gap-2 px-3 py-2 bg-white border border-slate-200 rounded-lg text-xs font-semibold text-slate-700 hover:bg-slate-50"
        >
          <RefreshCw className="w-3.5 h-3.5" /> Refresh
        </button>
      </div>

      <div className="flex gap-2">
        {tabs.map((t) => (
          <button
            key={t.key}
            onClick={() => setTab(t.key)}
            className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-colors ${
              tab === t.key ? 'bg-lumina-primary text-white' : 'bg-white border border-slate-200 text-slate-600 hover:bg-slate-50'
            }`}
          >
            {t.label}
          </button>
        ))}
      </div>

      {error && <div className="bg-rose-50 border border-rose-200 text-rose-700 text-xs px-4 py-3 rounded-lg">{error}</div>}

      <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-700">
            <thead className="bg-slate-50 text-slate-500 font-semibold border-b border-slate-200 uppercase tracking-wider text-[11px]">
              <tr>
                <th className="py-3.5 px-5">Helper</th>
                <th className="py-3.5 px-5">Document Type</th>
                <th className="py-3.5 px-5">Document</th>
                <th className="py-3.5 px-5">Submitted</th>
                <th className="py-3.5 px-5">Status</th>
                <th className="py-3.5 px-5 text-right">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {isLoading ? (
                <tr><td colSpan={6} className="py-8 text-center text-slate-400">Loading...</td></tr>
              ) : documents.length === 0 ? (
                <tr><td colSpan={6} className="py-8 text-center text-slate-400">No documents in this queue.</td></tr>
              ) : (
                documents.map((doc) => (
                  <tr key={doc.id} className="hover:bg-slate-50/80">
                    <td className="py-3.5 px-5 font-semibold text-slate-900">{doc.helper?.user?.name ?? '—'}</td>
                    <td className="py-3.5 px-5 capitalize">{doc.document_type.replace('_', ' ')}</td>
                    <td className="py-3.5 px-5">
                      <a
                        href={doc.document_url}
                        target="_blank"
                        rel="noreferrer"
                        className="inline-flex items-center gap-1 text-lumina-primary font-semibold hover:underline"
                      >
                        View <ExternalLink className="w-3 h-3" />
                      </a>
                    </td>
                    <td className="py-3.5 px-5">{new Date(doc.created_at).toLocaleDateString()}</td>
                    <td className="py-3.5 px-5"><StatusBadge status={doc.status} /></td>
                    <td className="py-3.5 px-5 text-right space-x-2">
                      {doc.status !== 'approved' && (
                        <button
                          onClick={() => handleAction(doc.id, 'approve')}
                          disabled={actioningId === doc.id}
                          className="inline-flex items-center gap-1 px-2.5 py-1 rounded-md bg-emerald-50 text-emerald-700 font-semibold hover:bg-emerald-100 disabled:opacity-50"
                        >
                          <CheckCircle2 className="w-3.5 h-3.5" /> Approve
                        </button>
                      )}
                      {doc.status !== 'rejected' && (
                        <button
                          onClick={() => handleAction(doc.id, 'reject')}
                          disabled={actioningId === doc.id}
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
