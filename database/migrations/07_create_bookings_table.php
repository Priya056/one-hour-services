<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     * Table: bookings
     */
    public function up(): void
    {
        Schema::create('bookings', function (Blueprint $table) {
            $table->id();
            $table->foreignId('customer_id')->constrained('users')->onDelete('restrict');
            $table->foreignId('helper_id')->constrained('helper_profiles')->onDelete('restrict');
            $table->foreignId('category_id')->constrained('categories')->onDelete('restrict');
            $table->dateTime('scheduled_time');
            $table->decimal('duration_hours', 4, 2)->default(1.00);
            $table->enum('status', [
                'requested',
                'accepted',
                'rejected',
                'on_the_way',
                'in_progress',
                'completed',
                'cancelled'
            ])->default('requested');
            $table->decimal('location_lat', 10, 8);
            $table->decimal('location_lng', 11, 8);
            $table->text('address_text');
            $table->decimal('total_amount', 10, 2);
            $table->timestamps();

            // Indexes for fast lookup and status filtering
            $table->index('status');
            $table->index('customer_id');
            $table->index('helper_id');
            $table->index('scheduled_time');

            // Spatial lat/lng composite range index (Option 1)
            $table->index(['location_lat', 'location_lng']);
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('bookings');
    }
};
