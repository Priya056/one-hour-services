import React from 'react';

export type StatusType = 
  | 'completed' | 'active' | 'approved' | 'success' 
  | 'pending' | 'investigating' | 'in_progress'
  | 'rejected' | 'cancelled' | 'disputed' | 'blocked' | 'suspended' | 'open';

interface StatusBadgeProps {
  status: StatusType;
  label?: string;
  size?: 'sm' | 'md';
}

export const StatusBadge: React.FC<StatusBadgeProps> = ({ status, label, size = 'sm' }) => {
  const getStyles = (st: StatusType) => {
    switch (st) {
      case 'completed':
      case 'active':
      case 'approved':
      case 'success':
        return 'bg-emerald-50 text-emerald-700 border-emerald-200';

      case 'pending':
      case 'investigating':
      case 'in_progress':
        return 'bg-amber-50 text-amber-700 border-amber-200';

      case 'rejected':
      case 'cancelled':
      case 'disputed':
      case 'blocked':
      case 'suspended':
      case 'open':
        return 'bg-rose-50 text-rose-700 border-rose-200';

      default:
        return 'bg-slate-50 text-slate-700 border-slate-200';
    }
  };

  const formatText = (st: StatusType) => {
    if (label) return label;
    return st.replace('_', ' ').toUpperCase();
  };

  const padding = size === 'sm' ? 'px-2 py-0.5 text-[11px]' : 'px-2.5 py-1 text-xs';

  return (
    <span
      className={`inline-flex items-center gap-1.5 font-semibold rounded-full border shadow-2xs ${padding} ${getStyles(
        status
      )}`}
    >
      <span className={`w-1.5 h-1.5 rounded-full ${
        ['completed', 'active', 'approved', 'success'].includes(status) ? 'bg-emerald-500' :
        ['pending', 'investigating', 'in_progress'].includes(status) ? 'bg-amber-500 animate-pulse' :
        'bg-rose-500'
      }`}></span>
      {formatText(status)}
    </span>
  );
};
