CREATE TABLE IF NOT EXISTS user_stats (
	id BIGINT AUTO_INCREMENT,
    energy DOUBLE NOT NULL,
    tempo DOUBLE NOT NULL,
    mainstream DOUBLE NOT NULL,
    boringness DOUBLE NOT NULL,
    points BIGINT NOT NULL,
    battle_count INT NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS user_info (
	id BIGINT AUTO_INCREMENT,
    fb VARCHAR(100),
    ig VARCHAR(100),
    twitter VARCHAR(100),
    longitude DOUBLE,
    latitude DOUBLE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users (
	id BIGINT AUTO_INCREMENT,
    external_id VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    image_url VARCHAR(100) NOT NULL,
    join_date DATETIME NOT NULL,
    user_stats_id BIGINT,
    user_info_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (user_stats_id) REFERENCES user_stats(id),
    FOREIGN KEY (user_info_id) REFERENCES user_info(id)
);


CREATE TABLE IF NOT EXISTS user_friends (
	user_id BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (friend_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS beta_users (
	id BIGINT AUTO_INCREMENT,
    full_name VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    date DATETIME NOT NULL,
    active BOOLEAN NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS user_tracks (
	id BIGINT AUTO_INCREMENT,
    user_id BIGINT,
    time_range VARCHAR(6) NOT NULL,
    tracks JSON NOT NULL,
    date DATE NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (user_id, time_range, date),
	KEY (user_id)
);

CREATE TABLE IF NOT EXISTS user_artists (
	id BIGINT AUTO_INCREMENT,
    user_id BIGINT,
    time_range VARCHAR(6) NOT NULL,
    artists JSON NOT NULL,
    date DATE NOT NULL,
    PRIMARY KEY (id),
	UNIQUE KEY (user_id, time_range, date),
	KEY (user_id)
);

CREATE TABLE IF NOT EXISTS user_genres (
	id BIGINT AUTO_INCREMENT,
    user_id BIGINT,
    time_range VARCHAR(6) NOT NULL,
    genres JSON NOT NULL,
    date DATE NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (user_id, time_range, date),
	KEY (user_id)
);

CREATE EVENT IF NOT EXISTS battle_event
ON SCHEDULE EVERY 1 DAY
STARTS (TIMESTAMP(CURRENT_DATE))
DO
UPDATE user_stats SET battle_count = 0;