CREATE TABLE IF NOT EXISTS `news` (
    `news_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `place_id` BIGINT,
    `keyword` VARCHAR(30) NOT NULL,
    `title` VARCHAR(255) NOT NULL,
    `content` TEXT,
    `created_at` DATETIME(6),
    `updated_at` DATETIME(6),
    FOREIGN KEY (`place_id`) REFERENCES `place` (`place_id`) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `introduce` (
    `introduce_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `place_id` BIGINT,
    `content` TEXT,
    `created_at` DATETIME(6),
    `updated_at` DATETIME(6),
    FOREIGN KEY (`place_id`) REFERENCES `place` (`place_id`) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;