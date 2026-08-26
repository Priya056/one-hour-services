<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     * Table: helper_services
     */
    public function up(): void
    {
        Schema::create('helper_services', function (Blueprint $table) {
            $table->id();
            $table->foreignId('helper_id')->constrained('helper_profiles')->onDelete('cascade');
            $table->foreignId('category_id')->constrained('categories')->onDelete('restrict');
            $table->decimal('hourly_rate', 10, 2);
            $table->boolean('is_active')->default(true);
            $table->timestamps();
            $table->softDeletes();

            // Constraint: 1 rate per helper per category (Decision C)
            $table->unique(['helper_id', 'category_id']);

            // Indexes for search and filter optimization
            $table->index('category_id');
            $table->index('is_active');
            $table->index('hourly_rate');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('helper_services');
    }
};
