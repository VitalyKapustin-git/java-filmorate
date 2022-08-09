CREATE TABLE IF NOT EXISTS users
(
    id identity PRIMARY KEY,
    login varchar,
    email varchar,
    name varchar,
    birthday date
);

CREATE TABLE IF NOT EXISTS films
(
    id identity PRIMARY KEY,
    rate float,
    name varchar,
    description varchar,
    release_date date,
    duration float,
    mpa_id int
);

CREATE TABLE IF NOT EXISTS mpa
(
    id identity PRIMARY KEY,
    mpa varchar
);

CREATE TABLE IF NOT EXISTS genres
(
    id identity PRIMARY KEY,
    genre varchar
);

CREATE TABLE IF NOT EXISTS friends
(
  usr_id int,
  friend_id int,
  approved bytea,
  PRIMARY KEY (usr_id, friend_id),
  FOREIGN KEY (usr_id) REFERENCES users(id),
  FOREIGN KEY (friend_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS film_rates
(
    usr_id int,
    film_id int,
    PRIMARY KEY (usr_id, film_id),
    FOREIGN KEY (usr_id) REFERENCES users(id),
    FOREIGN KEY (film_id) REFERENCES films(id)
);

CREATE TABLE IF NOT EXISTS film_genre
(
    film_id int,
    genre_id int,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films(id),
    FOREIGN KEY (genre_id) REFERENCES genres(id)
);