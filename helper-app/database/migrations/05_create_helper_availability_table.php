<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     * Table: helper_availability
     */
    public function up(): void
    {
        Schema::create('helper_availability', function (Blueprint $table) {
            $table->id();
            $table->foreignId('helper_id')->constrained('helper_profiles')->onDelete('cascade');
            $table->unsignedTinyInteger('day_of_week')->comment('0=Sunday, 1=Monday, 2=Tuesday, 3=Wednesday, 4=Thursday, 5=Friday, 6=Saturday');
            $table->time('start_time');
            $table->time('end_time');
            $table->timestamps();

            // Composite index for fast schedule checking
            $table->index(['helper_id', 'day_of_week']);
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('helper_availability');
    }
};
