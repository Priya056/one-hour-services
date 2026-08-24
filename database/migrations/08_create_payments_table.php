<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     * Table: payments
     */
    public function up(): void
    {
        Schema::create('payments', function (Blueprint $table) {
            $table->id();
            $table->foreignId('booking_id')->unique()->constrained('bookings')->onDelete('restrict');
            $table->decimal('amount', 10, 2);
            $table->decimal('platform_commission', 10, 2);
            $table->decimal('helper_payout_amount', 10, 2);
            $table->enum('payment_gateway', ['razorpay', 'stripe']);
            $table->string('gateway_transaction_id')->nullable();
            $table->enum('status', ['pending', 'success', 'failed', 'refunded'])->default('pending');
            $table->timestamps();

            // Indexes for transaction lookup and reconciliation
            $table->index('status');
            $table->index('gateway_transaction_id');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('payments');
    }
};
