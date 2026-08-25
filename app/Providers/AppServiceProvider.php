<?php

namespace App\Providers;

use Illuminate\Support\ServiceProvider;
use Illuminate\Support\Facades\Gate;
use App\Models\User;
use App\Models\HelperProfile;
use App\Models\KYCDocument;
use App\Models\HelperService;
use App\Models\HelperAvailability;
use App\Models\Booking;
use App\Policies\UserPolicy;
use App\Policies\HelperProfilePolicy;
use App\Policies\KYCDocumentPolicy;
use App\Policies\HelperServicePolicy;
use App\Policies\HelperAvailabilityPolicy;
use App\Observers\BookingObserver;

class AppServiceProvider extends ServiceProvider
{
    /**
     * Register any application services.
     */
    public function register(): void
    {
        //
    }

    /**
     * Bootstrap any application services.
     */
    public function boot(): void
    {
        Gate::policy(User::class, UserPolicy::class);
        Gate::policy(HelperProfile::class, HelperProfilePolicy::class);
        Gate::policy(KYCDocument::class, KYCDocumentPolicy::class);
        Gate::policy(HelperService::class, HelperServicePolicy::class);
        Gate::policy(HelperAvailability::class, HelperAvailabilityPolicy::class);

        // Register observers
        Booking::observe(BookingObserver::class);
    }
}
