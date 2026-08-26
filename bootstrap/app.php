<?php

use Illuminate\Foundation\Application;
use Illuminate\Foundation\Configuration\Exceptions;
use Illuminate\Foundation\Configuration\Middleware;

return Application::configure(basePath: dirname(__DIR__))
    ->withRouting(
        web: __DIR__.'/../routes/web.php',
        api: __DIR__.'/../routes/api.php',
        commands: __DIR__.'/../routes/console.php',
        health: '/up',
    )
    ->withMiddleware(function (Middleware $middleware): void {
        // Note: EnsureFrontendRequestsAreStateful is NOT prepended for mobile apps
        // Mobile apps use bearer tokens (stateless) via Sanctum
        // This middleware is only for SPA (Single Page Applications) using cookies

        $middleware->alias([
            'verified' => \Illuminate\Auth\Middleware\EnsureEmailIsVerified::class,
            'role' => \App\Http\Middleware\RoleMiddleware::class,
            'check.helper.availability' => \App\Http\Middleware\CheckHelperAvailability::class,
        ]);

        // Render (and most PaaS hosts) terminate TLS at a reverse proxy and
        // forward plain HTTP to the container, signalling the original
        // scheme via X-Forwarded-Proto. Without trusting that header,
        // url()/redirect() see an insecure request and generate http://
        // links even for a request that arrived over https — which Android
        // then correctly refuses to follow (cleartext traffic disabled).
        $middleware->trustProxies(at: '*');
    })
    ->withExceptions(function (Exceptions $exceptions): void {
        // Every api/* route is a JSON API — always render exceptions
        // (validation failures included) as JSON, never as an HTML
        // redirect/error page, regardless of the request's Accept header.
        $exceptions->shouldRenderJsonWhen(function ($request, $throwable) {
            return $request->is('api/*') || $request->expectsJson();
        });
    })->create();
