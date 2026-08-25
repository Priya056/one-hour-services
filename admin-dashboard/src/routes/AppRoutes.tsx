import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { DashboardLayout } from '../layouts/DashboardLayout';
import { DashboardPage } from '../pages/DashboardPage';
import {
  UsersPage,
  HelpersPage,
  KycApprovalsPage,
  CategoriesPage,
  BookingsPage,
  PaymentsPage,
  CommissionSettingsPage,
  RefundsPage,
  ComplaintsPage,
  ReviewsPage,
  ReportsAnalyticsPage,
  LocationsPage,
} from '../pages/Stubs';

export const AppRoutes: React.FC = () => {
  return (
    <Routes>
      <Route path="/" element={<DashboardLayout />}>
        <Route index element={<DashboardPage />} />
        <Route path="users" element={<UsersPage />} />
        <Route path="helpers" element={<HelpersPage />} />
        <Route path="kyc-approvals" element={<KycApprovalsPage />} />
        <Route path="categories" element={<CategoriesPage />} />
        <Route path="bookings" element={<BookingsPage />} />
        <Route path="payments" element={<PaymentsPage />} />
        <Route path="commission-settings" element={<CommissionSettingsPage />} />
        <Route path="refunds" element={<RefundsPage />} />
        <Route path="complaints" element={<ComplaintsPage />} />
        <Route path="reviews" element={<ReviewsPage />} />
        <Route path="reports" element={<ReportsAnalyticsPage />} />
        <Route path="locations" element={<LocationsPage />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Route>
    </Routes>
  );
};
