-- =============================================================================
-- 1-Hour Local Services Marketplace — MySQL Database Schema (DDL)
-- Generated for Laravel/PHP Backend & MySQL 8.0+
-- =============================================================================

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `platform_settings`;
DROP TABLE IF EXISTS `complaints_disputes`;
DROP TABLE IF EXISTS `cancellations_refunds`;
DROP TABLE IF EXISTS `notifications`;
DROP TABLE IF EXISTS `chat_messages`;
DROP TABLE IF EXISTS `reviews`;
DROP TABLE IF EXISTS `withdrawal_requests`;
DROP TABLE IF EXISTS `wallet_transactions`;
DROP TABLE IF EXISTS `wallets`;
DROP TABLE IF EXISTS `payments`;
DROP TABLE IF EXISTS `bookings`;
DROP TABLE IF EXISTS `kyc_documents`;
DROP TABLE IF EXISTS `helper_availability`;
DROP TABLE IF EXISTS `helper_services`;
DROP TABLE IF EXISTS `categories`;
DROP TABLE IF EXISTS `helper_profiles`;
DROP TABLE IF EXISTS `users`;

SET FOREIGN_KEY_CHECKS = 1;

-- -----------------------------------------------------------------------------
-- 1. users
-- -----------------------------------------------------------------------------
CREATE TABLE `users` (
  `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(255) NOT NULL,
  `phone` VARCHAR(20) NOT NULL UNIQUE,
  `email` VARCHAR(255) NULL UNIQUE,
  `password_hash` VARCHAR(255) NULL,
  `role` ENUM('customer', 'helper', 'admin') NOT NULL DEFAULT 'customer',
  `profile_photo_url` VARCHAR(2048) NULL,
  `address` TEXT NULL,
  `is_active` TINYINT(1) NOT NULL DEFAULT 1,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` TIMESTAMP NULL DEFAULT NULL,
  INDEX `users_role_index` (`role`),
  INDEX `users_is_active_index` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 2. helper_profiles
-- -----------------------------------------------------------------------------
CREATE TABLE `helper_profiles` (
  `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT UNSIGNED NOT NULL UNIQUE,
  `bio` TEXT NULL,
  `experience_years` TINYINT UNSIGNED NOT NULL DEFAULT 0,
  `is_available_now` TINYINT(1) NOT NULL DEFAULT 0,
  `service_radius_km` DECIMAL(5,2) NOT NULL DEFAULT 10.00,
  `average_rating` DECIMAL(3,2) NOT NULL DEFAULT 0.00,
  `total_reviews` INT UNSIGNED NOT NULL DEFAULT 0,
  `kyc_status` ENUM('pending', 'approved', 'rejected') NOT NULL DEFAULT 'pending',
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `helper_profiles_user_id_foreign` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  INDEX `helper_profiles_is_available_now_index` (`is_available_now`),
  INDEX `helper_profiles_kyc_status_index` (`kyc_status`),
  INDEX `helper_profiles_average_rating_index` (`average_rating`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 3. categories
-- -----------------------------------------------------------------------------
CREATE TABLE `categories` (
  `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(100) NOT NULL UNIQUE,
  `icon_url` VARCHAR(2048) NULL,
  `description` TEXT NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` TIMESTAMP NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 4. helper_services
-- -----------------------------------------------------------------------------
CREATE TABLE `helper_services` (
  `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `helper_id` BIGINT UNSIGNED NOT NULL,
  `category_id` BIGINT UNSIGNED NOT NULL,
  `hourly_rate` DECIMAL(10,2) NOT NULL,
  `is_active` TINYINT(1) NOT NULL DEFAULT 1,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` TIMESTAMP NULL DEFAULT NULL,
  UNIQUE KEY `helper_services_helper_id_category_id_unique` (`helper_id`, `category_id`),
  CONSTRAINT `helper_services_helper_id_foreign` FOREIGN KEY (`helper_id`) REFERENCES `helper_profiles` (`id`) ON DELETE CASCADE,
  CONSTRAINT `helper_services_category_id_foreign` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE RESTRICT,
  INDEX `helper_services_category_id_index` (`category_id`),
  INDEX `helper_services_is_active_index` (`is_active`),
  INDEX `helper_services_hourly_rate_index` (`hourly_rate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 5. helper_availability
-- -----------------------------------------------------------------------------
CREATE TABLE `helper_availability` (
  `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `helper_id` BIGINT UNSIGNED NOT NULL,
  `day_of_week` TINYINT UNSIGNED NOT NULL COMMENT '0=Sunday, 1=Monday, 2=Tuesday, 3=Wednesday, 4=Thursday, 5=Friday, 6=Saturday',
  `start_time` TIME NOT NULL,
  `end_time` TIME NOT NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `helper_availability_helper_id_foreign` FOREIGN KEY (`helper_id`) REFERENCES `helper_profiles` (`id`) ON DELETE CASCADE,
  INDEX `helper_availability_helper_id_day_of_week_index` (`helper_id`, `day_of_week`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 6. kyc_documents
-- -----------------------------------------------------------------------------
CREATE TABLE `kyc_documents` (
  `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `helper_id` BIGINT UNSIGNED NOT NULL,
  `document_type` VARCHAR(50) NOT NULL,
  `document_url` VARCHAR(2048) NOT NULL,
  `status` ENUM('pending', 'approved', 'rejected') NOT NULL DEFAULT 'pending',
  `reviewed_by` BIGINT UNSIGNED NULL,
  `reviewed_at` TIMESTAMP NULL DEFAULT NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `kyc_documents_helper_id_foreign` FOREIGN KEY (`helper_id`) REFERENCES `helper_profiles` (`id`) ON DELETE CASCADE,
  CONSTRAINT `kyc_documents_reviewed_by_foreign` FOREIGN KEY (`reviewed_by`) REFERENCES `users` (`id`) ON DELETE SET NULL,
  INDEX `kyc_documents_helper_id_index` (`helper_id`),
  INDEX `kyc_documents_status_index` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 7. bookings
-- -----------------------------------------------------------------------------
CREATE TABLE `bookings` (
  `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `customer_id` BIGINT UNSIGNED NOT NULL,
  `helper_id` BIGINT UNSIGNED NOT NULL,
  `category_id` BIGINT UNSIGNED NOT NULL,
  `scheduled_time` DATETIME NOT NULL,
  `duration_hours` DECIMAL(4,2) NOT NULL DEFAULT 1.00,
  `status` ENUM('requested', 'accepted', 'rejected', 'on_the_way', 'in_progress', 'completed', 'cancelled') NOT NULL DEFAULT 'requested',
  `location_lat` DECIMAL(10,8) NOT NULL,
  `location_lng` DECIMAL(11,8) NOT NULL,
  `address_text` TEXT NOT NULL,
  `total_amount` DECIMAL(10,2) NOT NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `bookings_customer_id_foreign` FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `bookings_helper_id_foreign` FOREIGN KEY (`helper_id`) REFERENCES `helper_profiles` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `bookings_category_id_foreign` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE RESTRICT,
  INDEX `bookings_status_index` (`status`),
  INDEX `bookings_customer_id_index` (`customer_id`),
  INDEX `bookings_helper_id_index` (`helper_id`),
  INDEX `bookings_scheduled_time_index` (`scheduled_time`),
  INDEX `bookings_location_lat_location_lng_index` (`location_lat`, `location_lng`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 8. payments
-- -----------------------------------------------------------------------------
CREATE TABLE `payments` (
  `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `booking_id` BIGINT UNSIGNED NOT NULL UNIQUE,
  `amount` DECIMAL(10,2) NOT NULL,
  `platform_commission` DECIMAL(10,2) NOT NULL,
  `helper_payout_amount` DECIMAL(10,2) NOT NULL,
  `payment_gateway` ENUM('razorpay', 'stripe') NOT NULL,
  `gateway_transaction_id` VARCHAR(255) NULL,
  `status` ENUM('pending', 'success', 'failed', 'refunded') NOT NULL DEFAULT 'pending',
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `payments_booking_id_foreign` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`) ON DELETE RESTRICT,
  INDEX `payments_status_index` (`status`),
  INDEX `payments_gateway_transaction_id_index` (`gateway_transaction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 9. wallets
-- -----------------------------------------------------------------------------
CREATE TABLE `wallets` (
  `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `helper_id` BIGINT UNSIGNED NOT NULL UNIQUE,
  `balance` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `last_updated_at` TIMESTAMP NULL DEFAULT NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `wallets_helper_id_foreign` FOREIGN KEY (`helper_id`) REFERENCES `helper_profiles` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 10. wallet_transactions
-- -----------------------------------------------------------------------------
CREATE TABLE `wallet_transactions` (
  `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `wallet_id` BIGINT UNSIGNED NOT NULL,
  `booking_id` BIGINT UNSIGNED NULL,
  `type` ENUM('credit', 'debit', 'withdrawal') NOT NULL,
  `amount` DECIMAL(10,2) NOT NULL,
  `status` ENUM('pending', 'completed', 'failed') NOT NULL DEFAULT 'completed',
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `wallet_transactions_wallet_id_foreign` FOREIGN KEY (`wallet_id`) REFERENCES `wallets` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `wallet_transactions_booking_id_foreign` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`) ON DELETE SET NULL,
  INDEX `wallet_transactions_wallet_id_index` (`wallet_id`),
  INDEX `wallet_transactions_type_index` (`type`),
  INDEX `wallet_transactions_status_index` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 11. withdrawal_requests
-- -----------------------------------------------------------------------------
CREATE TABLE `withdrawal_requests` (
  `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `helper_id` BIGINT UNSIGNED NOT NULL,
  `amount` DECIMAL(10,2) NOT NULL,
  `bank_account_details` JSON NOT NULL COMMENT 'Stores bank account or UPI ID payload',
  `status` ENUM('pending', 'processed', 'rejected') NOT NULL DEFAULT 'pending',
  `requested_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `processed_at` TIMESTAMP NULL DEFAULT NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `withdrawal_requests_helper_id_foreign` FOREIGN KEY (`helper_id`) REFERENCES `helper_profiles` (`id`) ON DELETE RESTRICT,
  INDEX `withdrawal_requests_helper_id_index` (`helper_id`),
  INDEX `withdrawal_requests_status_index` (`status`),
  INDEX `withdrawal_requests_requested_at_index` (`requested_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 12. reviews
-- -----------------------------------------------------------------------------
CREATE TABLE `reviews` (
  `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `booking_id` BIGINT UNSIGNED NOT NULL UNIQUE,
  `customer_id` BIGINT UNSIGNED NOT NULL,
  `helper_id` BIGINT UNSIGNED NOT NULL,
  `rating` TINYINT UNSIGNED NOT NULL COMMENT 'Rating from 1 to 5',
  `comment` TEXT NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `reviews_booking_id_foreign` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`) ON DELETE CASCADE,
  CONSTRAINT `reviews_customer_id_foreign` FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `reviews_helper_id_foreign` FOREIGN KEY (`helper_id`) REFERENCES `helper_profiles` (`id`) ON DELETE CASCADE,
  INDEX `reviews_customer_id_index` (`customer_id`),
  INDEX `reviews_helper_id_index` (`helper_id`),
  INDEX `reviews_rating_index` (`rating`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 13. chat_messages
-- -----------------------------------------------------------------------------
CREATE TABLE `chat_messages` (
  `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `booking_id` BIGINT UNSIGNED NOT NULL,
  `sender_id` BIGINT UNSIGNED NOT NULL,
  `message_text` TEXT NOT NULL,
  `sent_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_read` TINYINT(1) NOT NULL DEFAULT 0,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `chat_messages_booking_id_foreign` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chat_messages_sender_id_foreign` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  INDEX `chat_messages_booking_id_index` (`booking_id`),
  INDEX `chat_messages_sender_id_index` (`sender_id`),
  INDEX `chat_messages_is_read_index` (`is_read`),
  INDEX `chat_messages_sent_at_index` (`sent_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 14. notifications
-- -----------------------------------------------------------------------------
CREATE TABLE `notifications` (
  `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `body` TEXT NOT NULL,
  `type` ENUM('booking_update', 'payment', 'chat', 'promo') NOT NULL,
  `is_read` TINYINT(1) NOT NULL DEFAULT 0,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `notifications_user_id_foreign` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  INDEX `notifications_user_id_index` (`user_id`),
  INDEX `notifications_is_read_index` (`is_read`),
  INDEX `notifications_type_index` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 15. cancellations_refunds
-- -----------------------------------------------------------------------------
CREATE TABLE `cancellations_refunds` (
  `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `booking_id` BIGINT UNSIGNED NOT NULL UNIQUE,
  `cancelled_by` ENUM('customer', 'helper', 'admin') NOT NULL,
  `reason` TEXT NULL,
  `refund_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `refund_status` ENUM('none', 'pending', 'processed', 'failed') NOT NULL DEFAULT 'none',
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `cancellations_refunds_booking_id_foreign` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`) ON DELETE RESTRICT,
  INDEX `cancellations_refunds_cancelled_by_index` (`cancelled_by`),
  INDEX `cancellations_refunds_refund_status_index` (`refund_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 16. complaints_disputes
-- -----------------------------------------------------------------------------
CREATE TABLE `complaints_disputes` (
  `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `booking_id` BIGINT UNSIGNED NOT NULL,
  `raised_by` BIGINT UNSIGNED NOT NULL,
  `description` TEXT NOT NULL,
  `status` ENUM('open', 'investigating', 'resolved') NOT NULL DEFAULT 'open',
  `resolved_by` BIGINT UNSIGNED NULL,
  `resolved_at` TIMESTAMP NULL DEFAULT NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `complaints_disputes_booking_id_foreign` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `complaints_disputes_raised_by_foreign` FOREIGN KEY (`raised_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `complaints_disputes_resolved_by_foreign` FOREIGN KEY (`resolved_by`) REFERENCES `users` (`id`) ON DELETE SET NULL,
  INDEX `complaints_disputes_booking_id_index` (`booking_id`),
  INDEX `complaints_disputes_raised_by_index` (`raised_by`),
  INDEX `complaints_disputes_status_index` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 17. platform_settings
-- -----------------------------------------------------------------------------
CREATE TABLE `platform_settings` (
  `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `key` VARCHAR(100) NOT NULL UNIQUE,
  `value` TEXT NOT NULL,
  `description` VARCHAR(255) NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- Initial Seed Data
-- -----------------------------------------------------------------------------
INSERT INTO `categories` (`id`, `name`, `description`) VALUES
(1, 'Personal Assistance', 'Personal assistants, administrative help, scheduling, organizing'),
(2, 'Electrical', 'Minor electrical repairs, fixture installations, wiring troubleshooting'),
(3, 'Tutoring', '1-hour subject tutoring, language practice, assignment help'),
(4, 'Photography', 'Event photography, quick portrait sessions, product photos'),
(5, 'Home Repairs', 'Handyman services, furniture assembly, plumbing repairs'),
(6, 'Errands & Delivery', 'Pick up and drop off, local errands, grocery shopping'),
(7, 'Design/Creative', 'Graphic design edits, video clipping, quick logo adjustments'),
(8, 'Business/Professional', 'Resume editing, document formatting, tech support');

INSERT INTO `platform_settings` (`key`, `value`, `description`) VALUES
('default_commission_percent', '15.00', 'Percentage taken by the platform on each completed booking'),
('max_search_radius_km', '25.00', 'Maximum radius allowed for nearby helper lookup'),
('booking_cancellation_window_mins', '15', 'Free cancellation window in minutes after booking acceptance');
