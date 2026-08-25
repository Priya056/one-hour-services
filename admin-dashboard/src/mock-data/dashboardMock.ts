import { Booking } from '../types';

export interface TrendDataPoint {
  day: string;
  bookings: number;
  revenue: number;
}

export const mockTrendData: TrendDataPoint[] = [
  { day: 'Mon', bookings: 94, revenue: 2450 },
  { day: 'Tue', bookings: 112, revenue: 2980 },
  { day: 'Wed', bookings: 105, revenue: 2790 },
  { day: 'Thu', bookings: 130, revenue: 3410 },
  { day: 'Fri', bookings: 148, revenue: 3950 },
  { day: 'Sat', bookings: 165, revenue: 4420 },
  { day: 'Sun', bookings: 128, revenue: 3450 },
];

export const mockRecentBookings: Booking[] = [
  {
    id: 'b1',
    bookingNumber: '#LUM-9841',
    customerName: 'Priya Sharma',
    customerId: 'u101',
    helperName: 'Vikram Singh',
    helperId: 'h201',
    category: 'Electrician',
    amount: 45.0,
    commission: 6.75,
    helperPayout: 38.25,
    status: 'completed',
    date: '2026-08-25',
    time: '12:30 PM',
    location: 'Indiranagar, Bengaluru'
  },
  {
    id: 'b2',
    bookingNumber: '#LUM-9840',
    customerName: 'Ananya Verma',
    customerId: 'u102',
    helperName: 'Rohan Gupta',
    helperId: 'h202',
    category: 'Photographer',
    amount: 85.0,
    commission: 12.75,
    helperPayout: 72.25,
    status: 'in_progress',
    date: '2026-08-25',
    time: '01:00 PM',
    location: 'Koramangala, Bengaluru'
  },
  {
    id: 'b3',
    bookingNumber: '#LUM-9839',
    customerName: 'Rahul Mehta',
    customerId: 'u103',
    helperName: 'Suresh Kumar',
    helperId: 'h203',
    category: 'Home Repairs',
    amount: 55.0,
    commission: 8.25,
    helperPayout: 46.75,
    status: 'pending',
    date: '2026-08-25',
    time: '01:15 PM',
    location: 'HSR Layout, Bengaluru'
  },
  {
    id: 'b4',
    bookingNumber: '#LUM-9838',
    customerName: 'Neha Kapoor',
    customerId: 'u104',
    helperName: 'Kavita Patel',
    helperId: 'h204',
    category: 'Tutor',
    amount: 40.0,
    commission: 6.0,
    helperPayout: 34.0,
    status: 'completed',
    date: '2026-08-25',
    time: '11:00 AM',
    location: 'Whitefield, Bengaluru'
  },
  {
    id: 'b5',
    bookingNumber: '#LUM-9837',
    customerName: 'Karan Malhotra',
    customerId: 'u105',
    helperName: 'Amit Shah',
    helperId: 'h205',
    category: 'Personal Assistant',
    amount: 60.0,
    commission: 9.0,
    helperPayout: 51.0,
    status: 'disputed',
    date: '2026-08-25',
    time: '10:15 AM',
    location: 'Jayanagar, Bengaluru'
  },
];
