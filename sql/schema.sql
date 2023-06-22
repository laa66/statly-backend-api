CREATE TABLE IF NOT EXISTS users (
	id BIGINT AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    image_url VARCHAR(100) NOT NULL,
    points BIGINT NOT NULL,
    join_date DATETIME NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS beta_users (
	id BIGINT AUTO_INCREMENT,
    full_name VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    date DATETIME NOT NULL,
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
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_artists (
	id BIGINT AUTO_INCREMENT,
    user_id BIGINT,
    time_range VARCHAR(6) NOT NULL,
    artists JSON NOT NULL,
    date DATE NOT NULL,
    PRIMARY KEY (id),
	UNIQUE KEY (user_id, time_range, date),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_genres (
	id BIGINT AUTO_INCREMENT,
    user_id BIGINT,
    time_range VARCHAR(6) NOT NULL,
    genres JSON NOT NULL,
    date DATE NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (user_id, time_range, date),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_friends (
	user_id BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (friend_id) REFERENCES users(id)
);