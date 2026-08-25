export interface User {
  id: string;
  name: string;
  email: string;
  phone: string;
  role: 'customer' | 'helper' | 'admin';
  status: 'active' | 'blocked';
  joinedDate: string;
  avatarUrl?: string;
  totalBookings: number;
}

export interface Helper {
  id: string;
  userId: string;
  name: string;
  email: string;
  phone: string;
  category: string;
  hourlyRate: number;
  rating: number;
  totalReviews: number;
  completedJobs: number;
  kycStatus: 'approved' | 'pending' | 'rejected';
  status: 'active' | 'suspended';
  location: string;
  avatarUrl?: string;
}

export type BookingStatus = 'completed' | 'pending' | 'in_progress' | 'cancelled' | 'disputed';

export interface Booking {
  id: string;
  bookingNumber: string;
  customerName: string;
  customerId: string;
  helperName: string;
  helperId: string;
  category: string;
  amount: number;
  commission: number;
  helperPayout: number;
  status: BookingStatus;
  date: string;
  time: string;
  location: string;
}

export interface KycDocument {
  id: string;
  helperId: string;
  helperName: string;
  category: string;
  documentType: 'Government ID' | 'Background Check' | 'Certification' | 'Proof of Address';
  documentUrl: string;
  submittedAt: string;
  status: 'pending' | 'approved' | 'rejected';
  rejectionReason?: string;
}

export interface Category {
  id: string;
  name: string;
  iconName: string;
  description: string;
  activeHelpersCount: number;
  commissionRate: number;
  status: 'active' | 'inactive';
}
