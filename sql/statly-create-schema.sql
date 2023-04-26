CREATE TABLE IF NOT EXISTS users (
	id BIGINT AUTO_INCREMENT,
    email VARCHAR(50) NOT NULL,
    join_date DATETIME NOT NULL,
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

CREATE TABLE IF NOT EXISTS user_mainstream (
	id BIGINT AUTO_INCREMENT, 
    user_id BIGINT,
    time_range VARCHAR(6) NOT NULL,
    score DOUBLE NOT NULL,
    date DATE NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (user_id, time_range, date),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);