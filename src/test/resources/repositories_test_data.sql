CREATE TABLE IF NOT EXISTS users (
	id BIGINT AUTO_INCREMENT,
    email VARCHAR(50) NOT NULL,
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

INSERT INTO users(id, email, join_date) VALUES (null, 'user@mail.com', '2023-04-20 14:56:32');

INSERT INTO user_tracks(id, user_id, time_range, tracks, date) VALUES (null, 1, 'short', '{"artist_track1":1, "artist_track2":2, "artist_track3":3}', '2023-04-20');
INSERT INTO user_tracks(id, user_id, time_range, tracks, date) VALUES (null, 1, 'short', '{"artist_track1":1, "artist_track2":2, "artist_track3":3}', '2023-04-18');

INSERT INTO user_artists(id, user_id, time_range, artists, date) VALUES (null, 1, 'short', '{"artist1":1, "artist2":2, "artist3":3}', '2023-04-20');
INSERT INTO user_artists(id, user_id, time_range, artists, date) VALUES (null, 1, 'short', '{"artist1":1, "artist2":2, "artist3":3}', '2023-04-18');

INSERT INTO user_genres(id, user_id, time_range, genres, date) VALUES (null, 1, 'short', '{"genre1":20, "genre2":25, "genre3":35}', '2023-04-20');
INSERT INTO user_genres(id, user_id, time_range, genres, date) VALUES (null, 1, 'short', '{"genre1":20, "genre2":25, "genre3":35}', '2023-04-18');

INSERT INTO user_mainstream(id, user_id, time_range, score, date) VALUES (null, 1, 'short', 50.50, '2023-04-20');
INSERT INTO user_mainstream(id, user_id, time_range, score, date) VALUES (null, 1, 'short', 50.50, '2023-04-18');