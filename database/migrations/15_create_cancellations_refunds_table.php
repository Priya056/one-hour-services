<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     * Table: cancellations_refunds
     */
    public function up(): void
    {
        Schema::create('cancellations_refunds', function (Blueprint $table) {
            $table->id();
            $table->foreignId('booking_id')->unique()->constrained('bookings')->onDelete('restrict');
            $table->enum('cancelled_by', ['customer', 'helper', 'admin']);
            $table->text('reason')->nullable();
            $table->decimal('refund_amount', 10, 2)->default(0.00);
            $table->enum('refund_status', ['none', 'pending', 'processed', 'failed'])->default('none');
            $table->timestamps();

            // Indexes for cancellation audit and refund status tracking
            $table->index('cancelled_by');
            $table->index('refund_status');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('cancellations_refunds');
    }
};
