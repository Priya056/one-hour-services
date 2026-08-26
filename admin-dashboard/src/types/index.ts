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
  paymentStatus: 'paid' | 'pending' | 'failed';
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

export interface Refund {
  id: string;
  bookingNumber: string;
  customerName: string;
  helperName: string;
  amount: number;
  reason: string;
  requestedAt: string;
  status: 'pending' | 'approved' | 'declined';
}

export interface Complaint {
  id: string;
  ticketNumber: string;
  customerName: string;
  helperName: string;
  category: string;
  issueType: 'Late Arrival' | 'Poor Service Quality' | 'Billing Dispute' | 'Unprofessional Behavior';
  priority: 'high' | 'medium' | 'low';
  description: string;
  status: 'open' | 'under_review' | 'resolved';
  createdAt: string;
}

export interface Review {
  id: string;
  customerName: string;
  helperName: string;
  category: string;
  rating: number;
  comment: string;
  createdAt: string;
  status: 'published' | 'flagged' | 'hidden';
}

export interface CommissionSetting {
  id: string;
  categoryId: string;
  categoryName: string;
  defaultCommissionRate: number;
  minCommissionAmount: number;
  isSurgeEnabled: boolean;
}

export interface LocationArea {
  id: string;
  cityName: string;
  zoneName: string;
  activeHelpersCount: number;
  serviceRadiusKm: number;
  status: 'active' | 'expanding' | 'paused';
}

