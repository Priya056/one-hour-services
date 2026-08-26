import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { admin } = useAuth();
  if (!admin) {
    return <Navigate to="/login" replace />;
  }
  return <>{children}</>;
};
