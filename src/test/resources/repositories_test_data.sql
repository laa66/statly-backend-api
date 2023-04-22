INSERT INTO users(id, email, join_date) VALUES (1, 'user@mail.com', '2023-04-20 14:56:32');

INSERT INTO user_tracks(id, user_id, time_range, tracks, date) VALUES (1, 1, 'short', JSON '{"artist_track1":1, "artist_track2":2, "artist_track3":3}', '2023-04-20');
INSERT INTO user_tracks(id, user_id, time_range, tracks, date) VALUES (2, 1, 'short', JSON '{"artist_track1":1, "artist_track2":2, "artist_track3":3}', '2023-04-18');

INSERT INTO user_artists(id, user_id, time_range, artists, date) VALUES (1, 1, 'short', JSON '{"artist1":1, "artist2":2, "artist3":3}', '2023-04-20');
INSERT INTO user_artists(id, user_id, time_range, artists, date) VALUES (2, 1, 'short', JSON '{"artist1":1, "artist2":2, "artist3":3}', '2023-04-18');

INSERT INTO user_genres(id, user_id, time_range, genres, date) VALUES (1, 1, 'short', JSON '{"genre1":20, "genre2":25, "genre3":35}', '2023-04-20');
INSERT INTO user_genres(id, user_id, time_range, genres, date) VALUES (2, 1, 'short', JSON '{"genre1":20, "genre2":25, "genre3":35}', '2023-04-18');

INSERT INTO user_mainstream(id, user_id, time_range, score, date) VALUES (1, 1, 'short', 50.50, '2023-04-20');
INSERT INTO user_mainstream(id, user_id, time_range, score, date) VALUES (2, 1, 'short', 50.50, '2023-04-18');