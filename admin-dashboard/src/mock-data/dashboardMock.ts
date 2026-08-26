import { Booking } from '../types';

export interface TrendDataPoint {
  day: string;
  bookings: number;
  revenue: number;
}

export const mockTrendData: TrendDataPoint[] = [
  { day: 'Mon', bookings: 94, revenue: 24500 },
  { day: 'Tue', bookings: 112, revenue: 29800 },
  { day: 'Wed', bookings: 105, revenue: 27900 },
  { day: 'Thu', bookings: 130, revenue: 34100 },
  { day: 'Fri', bookings: 148, revenue: 39500 },
  { day: 'Sat', bookings: 165, revenue: 44200 },
  { day: 'Sun', bookings: 128, revenue: 34500 },
];

export const mockRecentBookings: Booking[] = [
  {
    id: 'b101',
    bookingNumber: '#LUM-101',
    customerName: 'Priya Sharma',
    customerId: 'u101',
    helperName: 'Alex Rivera',
    helperId: 'h1',
    category: 'Electrical Specialist',
    amount: 500.0, // ₹500
    commission: 75.0,
    helperPayout: 425.0,
    status: 'completed',
    paymentStatus: 'paid',
    date: '2026-08-26',
    time: '02:00 PM',
    location: 'Banjara Hills, Hyderabad'
  },
  {
    id: 'b102',
    bookingNumber: '#LUM-102',
    customerName: 'Ananya Verma',
    customerId: 'u102',
    helperName: 'Sarah Jenkins',
    helperId: 'h2',
    category: 'Errands & Delivery',
    amount: 350.0, // ₹350
    commission: 52.5,
    helperPayout: 297.5,
    status: 'completed',
    paymentStatus: 'paid',
    date: '2026-08-25',
    time: '11:00 AM',
    location: 'Jubilee Hills, Hyderabad'
  },
  {
    id: 'b103',
    bookingNumber: '#LUM-103',
    customerName: 'Priya Sharma',
    customerId: 'u101',
    helperName: 'Marcus Vance',
    helperId: 'h3',
    category: 'Photography',
    amount: 1200.0, // ₹1200
    commission: 180.0,
    helperPayout: 1020.0,
    status: 'pending',
    paymentStatus: 'pending',
    date: '2026-08-26',
    time: '05:00 PM',
    location: 'Gachibowli, Hyderabad'
  },
  {
    id: 'b104',
    bookingNumber: '#LUM-104',
    customerName: 'Rahul Mehta',
    customerId: 'u103',
    helperName: 'Alex Rivera',
    helperId: 'h1',
    category: 'Electrical Specialist',
    amount: 600.0, // ₹600
    commission: 0.0,
    helperPayout: 0.0,
    status: 'cancelled',
    paymentStatus: 'failed',
    date: '2026-08-24',
    time: '04:30 PM',
    location: 'Begumpet, Hyderabad'
  },
  {
    id: 'b105',
    bookingNumber: '#LUM-105',
    customerName: 'Ananya Verma',
    customerId: 'u102',
    helperName: 'David Chen',
    helperId: 'h4',
    category: 'Tutoring',
    amount: 600.0, // ₹600
    commission: 90.0,
    helperPayout: 510.0,
    status: 'completed',
    paymentStatus: 'paid',
    date: '2026-08-23',
    time: '10:00 AM',
    location: 'Hitec City, Hyderabad'
  }
];
