import React, { useEffect, useState } from 'react';
import { Outlet } from 'react-router-dom';
import { Sidebar } from '../components/layout/Sidebar';
import { Header } from '../components/layout/Header';
import { adminApi } from '../lib/adminApi';

export const DashboardLayout: React.FC = () => {
  const [pendingKycCount, setPendingKycCount] = useState(0);
  const [openComplaintsCount, setOpenComplaintsCount] = useState(0);

  useEffect(() => {
    adminApi.getKycDocuments('pending').then((docs) => setPendingKycCount(docs.length)).catch(() => {});
    adminApi.getDisputes().then((disputes) => {
      setOpenComplaintsCount(disputes.filter((d) => d.status !== 'resolved').length);
    }).catch(() => {});
  }, []);

  return (
    <div className="min-h-screen bg-lumina-tertiary flex">
      {/* Fixed Sidebar */}
      <Sidebar pendingKycCount={pendingKycCount} openComplaintsCount={openComplaintsCount} />

      {/* Main Container */}
      <div className="flex-1 pl-64 flex flex-col min-h-screen">
        {/* Fixed Header */}
        <Header />

        {/* Content Viewport */}
        <main className="flex-1 pt-16 p-8 overflow-y-auto">
          <Outlet />
        </main>
      </div>
    </div>
  );
};
