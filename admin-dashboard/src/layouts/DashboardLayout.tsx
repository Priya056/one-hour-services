import React from 'react';
import { Outlet } from 'react-router-dom';
import { Sidebar } from '../components/layout/Sidebar';
import { Header } from '../components/layout/Header';

export const DashboardLayout: React.FC = () => {
  return (
    <div className="min-h-screen bg-lumina-tertiary flex">
      {/* Fixed Sidebar */}
      <Sidebar pendingKycCount={5} openComplaintsCount={3} />

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
