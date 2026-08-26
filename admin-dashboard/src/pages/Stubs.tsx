import React from 'react';
const Stub: React.FC<{ title: string; description: string }> = ({ title, description }) => (
  <div className="bg-white rounded-xl p-6 border border-slate-200 shadow-sm">
    <h1 className="text-xl font-bold">{title}</h1>
    <p className="text-slate-500 text-sm mt-1">{description}</p>
    <p className="text-slate-400 text-xs mt-3">Not built yet — no backend endpoint wired here.</p>
  </div>
);
export const CategoriesPage: React.FC = () => <Stub title="Categories / Services" description="Configure service categories and commissions." />;
export const PaymentsPage: React.FC = () => <Stub title="Payments Management" description="Financial transactions, commissions, and helper payouts." />;
export const CommissionSettingsPage: React.FC = () => <Stub title="Commission Settings" description="Set platform commission percentages." />;
export const RefundsPage: React.FC = () => <Stub title="Refunds Management" description="Process customer refund requests and dispute settlements." />;
export const ReviewsPage: React.FC = () => <Stub title="Reviews Moderation" description="Moderate customer reviews and flag inappropriate content." />;
export const ReportsAnalyticsPage: React.FC = () => <Stub title="Reports & Analytics" description="Platform performance insights and metrics." />;
export const LocationsPage: React.FC = () => <Stub title="Locations / Service Areas" description="Manage operational cities and service zones." />;
