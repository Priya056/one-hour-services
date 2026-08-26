import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { DashboardLayout } from '../layouts/DashboardLayout';
import { DashboardPage } from '../pages/DashboardPage';
import { LoginPage } from '../pages/LoginPage';
import { HelpersPage } from '../pages/HelpersPage';
import { KycApprovalsPage } from '../pages/KycApprovalsPage';
import { UsersPage } from '../pages/UsersPage';
import { BookingsPage } from '../pages/BookingsPage';
import { ComplaintsPage } from '../pages/ComplaintsPage';
import {
  CategoriesPage,
  PaymentsPage,
  CommissionSettingsPage,
  RefundsPage,
  ReviewsPage,
  ReportsAnalyticsPage,
  LocationsPage,
} from '../pages/Stubs';
import { ProtectedRoute } from './ProtectedRoute';

export const AppRoutes: React.FC = () => {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <DashboardLayout />
          </ProtectedRoute>
        }
      >
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
