<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     * Table: complaints_disputes
     */
    public function up(): void
    {
        Schema::create('complaints_disputes', function (Blueprint $table) {
            $table->id();
            $table->foreignId('booking_id')->constrained('bookings')->onDelete('restrict');
            $table->foreignId('raised_by')->constrained('users')->onDelete('restrict');
            $table->text('description');
            $table->enum('status', ['open', 'investigating', 'resolved'])->default('open');
            $table->foreignId('resolved_by')->nullable()->constrained('users')->onDelete('set null');
            $table->timestamp('resolved_at')->nullable();
            $table->timestamps();

            // Indexes for dispute management dashboard
            $table->index('booking_id');
            $table->index('raised_by');
            $table->index('status');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('complaints_disputes');
    }
};
