import { api } from './api';

export interface ApiUser {
  id: number;
  name: string;
  phone: string;
  email: string | null;
  role: 'customer' | 'helper' | 'admin';
  profile_photo_url: string | null;
  address: string | null;
  is_active: boolean;
  created_at: string;
  updated_at: string;
}

export interface ApiHelperProfile {
  id: number;
  bio: string | null;
  experience_years: number;
  is_available_now: boolean;
  service_radius_km: string;
  average_rating: string | null;
  total_reviews: number | null;
  kyc_status: 'pending' | 'approved' | 'rejected';
  user: ApiUser | null;
  created_at: string;
  updated_at: string;
}

export interface ApiKycDocument {
  id: number;
  document_type: string;
  document_url: string;
  status: 'pending' | 'approved' | 'rejected';
  reviewed_at: string | null;
  created_at: string;
  updated_at: string;
  helper: ApiHelperProfile | null;
}

export interface ApiCategory {
  id: number;
  name: string;
  icon_url: string | null;
  description: string | null;
}

export interface ApiBooking {
  id: number;
  customer_id: number;
  helper_id: number;
  category_id: number;
  scheduled_time: string;
  duration_hours: number;
  status: string;
  address_text: string;
  total_amount: number;
  created_at: string;
  updated_at: string;
  customer: ApiUser | null;
  helper: ApiHelperProfile | null;
  category: ApiCategory | null;
}

export interface ApiDispute {
  id: number;
  booking_id: number;
  raised_by: number;
  description: string;
  status: 'open' | 'under_review' | 'resolved';
  resolved_by: number | null;
  resolved_at: string | null;
  created_at: string;
}

function unwrapList<T>(data: { data: T[] } | T[]): T[] {
  return Array.isArray(data) ? data : data.data;
}

export const adminApi = {
  getUsers: async (): Promise<ApiUser[]> => unwrapList((await api.get('/api/admin/users')).data),
  getHelpers: async (): Promise<ApiHelperProfile[]> => unwrapList((await api.get('/api/admin/helpers')).data),
  approveHelper: (id: number) => api.patch(`/api/admin/helpers/${id}/approve`),
  rejectHelper: (id: number) => api.patch(`/api/admin/helpers/${id}/reject`),

  getKycDocuments: async (status?: string): Promise<ApiKycDocument[]> =>
    unwrapList((await api.get('/api/admin/kyc', { params: status ? { status } : {} })).data),
  approveKyc: (id: number) => api.patch(`/api/admin/kyc/${id}/approve`),
  rejectKyc: (id: number) => api.patch(`/api/admin/kyc/${id}/reject`),

  getBookings: async (): Promise<ApiBooking[]> => unwrapList((await api.get('/api/bookings')).data),

  getDisputes: async (): Promise<ApiDispute[]> => unwrapList((await api.get('/api/admin/disputes')).data),
};
