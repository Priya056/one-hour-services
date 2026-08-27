import {
  User,
  Helper,
  Booking,
  KycDocument,
  Category,
  Refund,
  Complaint,
  Review,
  CommissionSetting,
  LocationArea
} from '../types';

export const mockUsers: User[] = [
  { id: 'u1', name: 'Priya Sharma', email: 'priya.sharma@example.com', phone: '+91 98765 43210', role: 'customer', status: 'active', joinedDate: '2026-01-15', totalBookings: 14 },
  { id: 'u2', name: 'Ananya Verma', email: 'ananya.v@example.com', phone: '+91 98123 45678', role: 'customer', status: 'active', joinedDate: '2026-02-01', totalBookings: 8 },
  { id: 'u3', name: 'Vikram Singh', email: 'vikram.s@example.com', phone: '+91 97654 32109', role: 'helper', status: 'active', joinedDate: '2025-11-20', totalBookings: 142 },
  { id: 'u4', name: 'Rohan Gupta', email: 'rohan.g@example.com', phone: '+91 96543 21098', role: 'helper', status: 'active', joinedDate: '2025-12-05', totalBookings: 89 },
  { id: 'u5', name: 'Rahul Mehta', email: 'rahul.m@example.com', phone: '+91 95432 10987', role: 'customer', status: 'blocked', joinedDate: '2026-03-10', totalBookings: 3 },
  { id: 'u6', name: 'Kavita Patel', email: 'kavita.p@example.com', phone: '+91 94321 09876', role: 'helper', status: 'active', joinedDate: '2026-01-08', totalBookings: 64 },
];

export const mockHelpers: Helper[] = [
  { id: 'h1', userId: 'u3', name: 'Vikram Singh', email: 'vikram.s@example.com', phone: '+91 97654 32109', category: 'Electrical Specialist', hourlyRate: 35.0, rating: 4.9, totalReviews: 124, completedJobs: 142, kycStatus: 'approved', status: 'active', location: 'Indiranagar, Bengaluru' },
  { id: 'h2', userId: 'u4', name: 'Rohan Gupta', email: 'rohan.g@example.com', phone: '+91 96543 21098', category: 'Photography', hourlyRate: 50.0, rating: 4.8, totalReviews: 67, completedJobs: 89, kycStatus: 'approved', status: 'active', location: 'Koramangala, Bengaluru' },
  { id: 'h3', userId: 'u6', name: 'Kavita Patel', email: 'kavita.p@example.com', phone: '+91 94321 09876', category: 'Tutoring', hourlyRate: 40.0, rating: 4.95, totalReviews: 54, completedJobs: 64, kycStatus: 'approved', status: 'active', location: 'Whitefield, Bengaluru' },
  { id: 'h4', userId: 'u7', name: 'Suresh Kumar', email: 'suresh.k@example.com', phone: '+91 93210 98765', category: 'Home Repairs', hourlyRate: 30.0, rating: 4.6, totalReviews: 32, completedJobs: 38, kycStatus: 'pending', status: 'active', location: 'HSR Layout, Bengaluru' },
  { id: 'h5', userId: 'u8', name: 'Amit Shah', email: 'amit.s@example.com', phone: '+91 92109 87654', category: 'Personal Assistance', hourlyRate: 25.0, rating: 4.2, totalReviews: 18, completedJobs: 22, kycStatus: 'rejected', status: 'suspended', location: 'Jayanagar, Bengaluru' },
];

export const mockKycDocs: KycDocument[] = [
  { id: 'kyc1', helperId: 'h4', helperName: 'Suresh Kumar', category: 'Home Repairs', documentType: 'Government ID', documentUrl: 'https://images.unsplash.com/photo-1554224155-8d04cb21cd6c', submittedAt: '2026-08-24 14:30', status: 'pending' },
  { id: 'kyc2', helperId: 'h5', helperName: 'Amit Shah', category: 'Personal Assistance', documentType: 'Background Check', documentUrl: 'https://images.unsplash.com/photo-1450133064473-71024230f91b', submittedAt: '2026-08-23 09:15', status: 'rejected', rejectionReason: 'Document image blurry, address mismatch' },
  { id: 'kyc3', helperId: 'h1', helperName: 'Vikram Singh', category: 'Electrical Specialist', documentType: 'Certification', documentUrl: 'https://images.unsplash.com/photo-1589829545856-d10d557cf95f', submittedAt: '2025-11-21 11:00', status: 'approved' },
];

export const mockCategories: Category[] = [
  { id: 'cat1', name: 'Personal Assistance', iconName: 'Person', description: 'Calendar, organization & daily tasks', activeHelpersCount: 42, commissionRate: 15.0, status: 'active' },
  { id: 'cat2', name: 'Electrical Specialist', iconName: 'Bolt', description: 'Wiring, fixtures & appliance repairs', activeHelpersCount: 68, commissionRate: 15.0, status: 'active' },
  { id: 'cat3', name: 'Tutoring', iconName: 'School', description: 'Math, Science & Languages', activeHelpersCount: 35, commissionRate: 12.0, status: 'active' },
  { id: 'cat4', name: 'Photography', iconName: 'Camera', description: 'Portraits, events & product shots', activeHelpersCount: 29, commissionRate: 18.0, status: 'active' },
  { id: 'cat5', name: 'Home Repairs', iconName: 'Build', description: 'Mounting, plumbing & carpentry', activeHelpersCount: 54, commissionRate: 15.0, status: 'active' },
  { id: 'cat6', name: 'Errands & Delivery', iconName: 'Truck', description: 'Groceries, pickups & document delivery', activeHelpersCount: 82, commissionRate: 10.0, status: 'active' },
];

export const mockRefunds: Refund[] = [
  { id: 'ref1', bookingNumber: '#LUM-9837', customerName: 'Karan Malhotra', helperName: 'Amit Shah', amount: 60.0, reason: 'Helper arrived 45 minutes late and job was incomplete', requestedAt: '2026-08-25 11:30 AM', status: 'pending' },
  { id: 'ref2', bookingNumber: '#LUM-9820', customerName: 'Sneha Roy', helperName: 'Suresh Kumar', amount: 30.0, reason: 'Accidental double booking by customer', requestedAt: '2026-08-24 04:15 PM', status: 'approved' },
  { id: 'ref3', bookingNumber: '#LUM-9812', customerName: 'Arjun Das', helperName: 'Vikram Singh', amount: 45.0, reason: 'Disagreed with hourly billing rounding', requestedAt: '2026-08-22 02:00 PM', status: 'declined' },
];

export const mockComplaints: Complaint[] = [
  { id: 'cmp1', ticketNumber: 'TCK-401', customerName: 'Karan Malhotra', helperName: 'Amit Shah', category: 'Personal Assistance', issueType: 'Late Arrival', priority: 'high', description: 'Helper arrived 45 minutes late without notice.', status: 'open', createdAt: '2026-08-25 11:45 AM' },
  { id: 'cmp2', ticketNumber: 'TCK-398', customerName: 'Meera Nair', helperName: 'Rohan Gupta', category: 'Photography', issueType: 'Billing Dispute', priority: 'medium', description: 'Extra hour charged without prior consent.', status: 'under_review', createdAt: '2026-08-24 03:20 PM' },
  { id: 'cmp3', ticketNumber: 'TCK-385', customerName: 'Deepak Joshi', helperName: 'Suresh Kumar', category: 'Home Repairs', issueType: 'Poor Service Quality', priority: 'low', description: 'Wall bracket was mounted slightly tilted.', status: 'resolved', createdAt: '2026-08-21 10:00 AM' },
];

export const mockReviews: Review[] = [
  { id: 'rev1', customerName: 'Priya Sharma', helperName: 'Vikram Singh', category: 'Electrical Specialist', rating: 5, comment: 'Punctual, professional, and solved my breaker issue in 20 minutes!', createdAt: '2026-08-25 01:10 PM', status: 'published' },
  { id: 'rev2', customerName: 'Ananya Verma', helperName: 'Rohan Gupta', category: 'Photography', rating: 5, comment: 'Amazing headshots! Fast turnaround within 1 hour.', createdAt: '2026-08-25 02:30 PM', status: 'published' },
  { id: 'rev3', customerName: 'Rahul Mehta', helperName: 'Amit Shah', category: 'Personal Assistance', rating: 1, comment: 'Unacceptable delays and unresponsive to phone calls.', createdAt: '2026-08-24 06:00 PM', status: 'flagged' },
];

export const mockCommissionSettings: CommissionSetting[] = [
  { id: 'cs1', categoryId: 'cat1', categoryName: 'Personal Assistance', defaultCommissionRate: 15.0, minCommissionAmount: 3.0, isSurgeEnabled: true },
  { id: 'cs2', categoryId: 'cat2', categoryName: 'Electrical Specialist', defaultCommissionRate: 15.0, minCommissionAmount: 5.0, isSurgeEnabled: true },
  { id: 'cs3', categoryId: 'cat3', categoryName: 'Tutoring', defaultCommissionRate: 12.0, minCommissionAmount: 4.0, isSurgeEnabled: false },
  { id: 'cs4', categoryId: 'cat4', categoryName: 'Photography', defaultCommissionRate: 18.0, minCommissionAmount: 8.0, isSurgeEnabled: true },
  { id: 'cs5', categoryId: 'cat5', categoryName: 'Home Repairs', defaultCommissionRate: 15.0, minCommissionAmount: 5.0, isSurgeEnabled: true },
  { id: 'cs6', categoryId: 'cat6', categoryName: 'Errands & Delivery', defaultCommissionRate: 10.0, minCommissionAmount: 2.0, isSurgeEnabled: false },
];

export const mockLocations: LocationArea[] = [
  { id: 'loc1', cityName: 'Bengaluru', zoneName: 'Indiranagar & Domlur', activeHelpersCount: 48, serviceRadiusKm: 10.0, status: 'active' },
  { id: 'loc2', cityName: 'Bengaluru', zoneName: 'Koramangala & HSR Layout', activeHelpersCount: 62, serviceRadiusKm: 12.0, status: 'active' },
  { id: 'loc3', cityName: 'Bengaluru', zoneName: 'Whitefield & ITPL', activeHelpersCount: 35, serviceRadiusKm: 15.0, status: 'active' },
  { id: 'loc4', cityName: 'Bengaluru', zoneName: 'Jayanagar & JP Nagar', activeHelpersCount: 29, serviceRadiusKm: 10.0, status: 'active' },
  { id: 'loc5', cityName: 'Mumbai', zoneName: 'Bandra & Khar West', activeHelpersCount: 15, serviceRadiusKm: 8.0, status: 'expanding' },
];
