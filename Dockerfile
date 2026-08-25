# Staging image for Render. Uses SQLite (no separate paid DB service needed);
# the file resets on every redeploy/restart — fine for API-contract testing,
# not a permanent store. See README for the tradeoff.
FROM php:8.3-cli

RUN apt-get update && apt-get install -y \
        git unzip libsqlite3-dev libzip-dev \
    && docker-php-ext-install pdo pdo_sqlite bcmath \
    && rm -rf /var/lib/apt/lists/*

COPY --from=composer:2 /usr/bin/composer /usr/bin/composer

WORKDIR /app
COPY . .

RUN composer install --no-dev --optimize-autoloader --no-interaction \
    && mkdir -p database \
    && touch database/database.sqlite \
    && chmod -R 775 storage bootstrap/cache

EXPOSE 8080

CMD php artisan config:clear \
    && php artisan migrate --force \
    && php artisan db:seed --force \
    && php artisan serve --host 0.0.0.0 --port ${PORT:-8080}
