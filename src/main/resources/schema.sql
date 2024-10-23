CREATE TABLE IF NOT EXISTS `user` (
    `user_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `email` VARCHAR(50) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `status` TINYINT(1) NOT NULL,
    `created_at` DATETIME(6),
    `updated_at` DATETIME(6),
    `type` VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS `place` (
    `place_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT,
    `place_name` VARCHAR(30) NOT NULL,
    `place_num` VARCHAR(30),
    `category` VARCHAR(50),
    `place_url` VARCHAR(225) NOT NULL,
    `profile_img` VARCHAR(225),
    `target_age` VARCHAR(225),
    `target` VARCHAR(225),
    `instagram_id` VARCHAR(50),
    `instagram_pw` VARCHAR(225),
    `created_at` DATETIME(6),
    `updated_at` DATETIME(6),
    FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
);

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

CREATE TABLE IF NOT EXISTS `feedback` (
    `feedback_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `place_id` BIGINT,
    `p_summary` VARCHAR(255),
    `p_body` TEXT,
    `n_summary` VARCHAR(255),
    `n_body` TEXT,
    `keyword` TEXT,
    `view_date` VARCHAR(10) NOT NULL,
    `created_at` DATETIME(6),
    `updated_at` DATETIME(6),
    FOREIGN KEY (`place_id`) REFERENCES `place` (`place_id`) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS `certification` (
    `email` VARCHAR(255) PRIMARY KEY,
    `certification_number` VARCHAR(255)
);
