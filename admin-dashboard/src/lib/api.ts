import axios from 'axios';

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'https://one-hour-services-backend-staging.onrender.com';
const TOKEN_KEY = 'lumina_admin_token';
const ADMIN_KEY = 'lumina_admin_user';

export interface AdminUser {
  id: number;
  name: string;
  phone: string;
  email: string | null;
  role: string;
}

export const auth = {
  getToken: (): string | null => localStorage.getItem(TOKEN_KEY),
  getAdmin: (): AdminUser | null => {
    const raw = localStorage.getItem(ADMIN_KEY);
    return raw ? JSON.parse(raw) : null;
  },
  isLoggedIn: (): boolean => !!localStorage.getItem(TOKEN_KEY),
  save: (token: string, user: AdminUser) => {
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(ADMIN_KEY, JSON.stringify(user));
  },
  clear: () => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(ADMIN_KEY);
  },
};

export const api = axios.create({ baseURL: BASE_URL });

api.interceptors.request.use((config) => {
  const token = auth.getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      auth.clear();
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export async function login(phone: string, password: string): Promise<AdminUser> {
  const response = await api.post('/api/login', { phone, password });
  const { token, user } = response.data;
  if (user.role !== 'admin') {
    throw new Error('This account is not an admin.');
  }
  auth.save(token, user);
  return user;
}

export function logout() {
  api.post('/api/logout').catch(() => {
    // Best-effort — clear local state regardless of network result.
  });
  auth.clear();
}
