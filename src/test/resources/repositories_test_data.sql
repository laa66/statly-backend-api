CREATE TABLE IF NOT EXISTS user_stats (
	id BIGINT AUTO_INCREMENT,
    energy DOUBLE NOT NULL,
    tempo DOUBLE NOT NULL,
    mainstream DOUBLE NOT NULL,
    boringness DOUBLE NOT NULL,
    points BIGINT NOT NULL,
    ig VARCHAR(100),
    fb VARCHAR(100),
    twitter VARCHAR(100),
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
    PRIMARY KEY (id),
    FOREIGN KEY (user_stats_id) REFERENCES user_stats(id)
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

INSERT INTO user_stats(id, energy, tempo, mainstream, boringness, points, ig, fb, twitter) VALUES (null, 50.0, 122.0, 32.50, 85.0, 500.0, 'ig', 'fb', null);
INSERT INTO users(id, external_id, username, email, image_url, join_date, user_stats_id) VALUES (null, 'id', 'username', 'user@mail.com', 'imageUrl', '2023-04-20 14:56:32', 1);

INSERT INTO user_stats(id, energy, tempo, mainstream, boringness, points, ig, fb, twitter) VALUES (null, 50.0, 122.0, 32.50, 85.0, 200.0, null, null, 'twitter');
INSERT INTO users(id, external_id, username, email, image_url, join_date, user_stats_id) VALUES (10, 'id', 'usernameTwo', 'userTwo@mail.com', 'imageUrl', '2023-04-20 14:33:32', 2);

INSERT INTO user_tracks(id, user_id, time_range, tracks, date) VALUES (null, 1, 'short', '{"artist_track1":1, "artist_track2":2, "artist_track3":3}', '2023-04-20');
INSERT INTO user_tracks(id, user_id, time_range, tracks, date) VALUES (null, 1, 'short', '{"artist_track1":1, "artist_track2":2, "artist_track3":3}', '2023-04-18');

INSERT INTO user_artists(id, user_id, time_range, artists, date) VALUES (null, 1, 'short', '{"artist1":1, "artist2":2, "artist3":3}', '2023-04-20');
INSERT INTO user_artists(id, user_id, time_range, artists, date) VALUES (null, 1, 'short', '{"artist1":1, "artist2":2, "artist3":3}', '2023-04-18');

INSERT INTO user_genres(id, user_id, time_range, genres, date) VALUES (null, 1, 'short', '{"genre1":20, "genre2":25, "genre3":35}', '2023-04-20');
INSERT INTO user_genres(id, user_id, time_range, genres, date) VALUES (null, 1, 'short', '{"genre1":20, "genre2":25, "genre3":35}', '2023-04-18');