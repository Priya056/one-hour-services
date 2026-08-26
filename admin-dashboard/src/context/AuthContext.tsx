import React, { createContext, useContext, useState } from 'react';
import { AdminUser, auth, login as apiLogin, logout as apiLogout } from '../lib/api';

interface AuthContextValue {
  admin: AdminUser | null;
  login: (phone: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [admin, setAdmin] = useState<AdminUser | null>(auth.getAdmin());

  const login = async (phone: string, password: string) => {
    const user = await apiLogin(phone, password);
    setAdmin(user);
  };

  const logout = () => {
    apiLogout();
    setAdmin(null);
  };

  return <AuthContext.Provider value={{ admin, login, logout }}>{children}</AuthContext.Provider>;
};

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
