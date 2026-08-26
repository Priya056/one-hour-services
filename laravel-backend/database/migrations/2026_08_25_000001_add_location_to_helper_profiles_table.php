<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     *
     * Helpers have no stored location today, so "who is available near me
     * right now" has nothing to search against. This adds the helper's last
     * reported live position, updated by the Helper app while available.
     */
    public function up(): void
    {
        Schema::table('helper_profiles', function (Blueprint $table) {
            $table->decimal('current_lat', 10, 8)->nullable()->after('service_radius_km');
            $table->decimal('current_lng', 11, 8)->nullable()->after('current_lat');
            $table->timestamp('location_updated_at')->nullable()->after('current_lng');

            $table->index(['current_lat', 'current_lng']);
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('helper_profiles', function (Blueprint $table) {
            $table->dropIndex(['current_lat', 'current_lng']);
            $table->dropColumn(['current_lat', 'current_lng', 'location_updated_at']);
        });
    }
};
