<?php

namespace App\Observers;

use App\Models\Booking;
use App\Models\Payment;
use App\Models\Wallet;
use App\Models\WalletTransaction;
use Illuminate\Support\Facades\DB;

class BookingObserver
{
    /**
     * Handle the Booking "updated" event.
     */
    public function updated(Booking $booking): void
    {
        // Check if status changed to completed
        if ($booking->wasChanged('status') && $booking->status === 'completed') {
            $this->creditHelperWallet($booking);
        }
    }

    /**
     * Credit helper wallet when booking is completed
     */
    protected function creditHelperWallet(Booking $booking): void
    {
        DB::transaction(function () use ($booking) {
            $payment = $booking->payment;

            if (!$payment || $payment->status !== 'success') {
                // No successful payment, cannot credit wallet
                return;
            }

            // Check if wallet transaction already exists for this booking
            $existingTransaction = WalletTransaction::where('booking_id', $booking->id)
                ->where('type', 'credit')
                ->first();

            if ($existingTransaction) {
                // Already credited, prevent duplicate
                return;
            }

            // Get or create helper wallet
            $wallet = Wallet::firstOrCreate(
                ['helper_id' => $booking->helper_id],
                ['balance' => 0.00]
            );

            // Credit wallet
            $wallet->increment('balance', $payment->helper_payout_amount);
            $wallet->touch(); // Update last_updated_at

            // Create wallet transaction
            WalletTransaction::create([
                'wallet_id' => $wallet->id,
                'booking_id' => $booking->id,
                'type' => 'credit',
                'amount' => $payment->helper_payout_amount,
                'status' => 'completed',
            ]);
        });
    }
}
