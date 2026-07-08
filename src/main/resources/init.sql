DROP TABLE IF EXISTS arrival_book    CASCADE;
DROP TABLE IF EXISTS arrival         CASCADE;
DROP TABLE IF EXISTS sale_book_copy  CASCADE;
DROP TABLE IF EXISTS sale            CASCADE;
DROP TABLE IF EXISTS book_copy_prices CASCADE;
DROP TABLE IF EXISTS book_copy       CASCADE;
DROP TABLE IF EXISTS book_genres     CASCADE;
DROP TABLE IF EXISTS book_author     CASCADE;
DROP TABLE IF EXISTS book            CASCADE;
DROP TABLE IF EXISTS genre           CASCADE;
DROP TABLE IF EXISTS author          CASCADE;

CREATE TABLE author (
    id         UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL
);

CREATE TABLE genre (
    id   UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE book (
    id           UUID         DEFAULT gen_random_uuid() PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    isbn         VARCHAR(13)  NOT NULL UNIQUE,
    description  TEXT,
    publish_date DATE         NOT NULL
);

CREATE TABLE book_genres (
    id       UUID PRIMARY KEY,
    book_id  UUID NOT NULL REFERENCES book(id)  ON DELETE CASCADE,
    genre_id UUID NOT NULL REFERENCES genre(id) ON DELETE CASCADE,
    UNIQUE (book_id, genre_id)
);

CREATE TABLE book_author (
    id        UUID PRIMARY KEY,
    book_id   UUID NOT NULL REFERENCES book(id)   ON DELETE CASCADE,
    author_id UUID NOT NULL REFERENCES author(id) ON DELETE CASCADE,
    UNIQUE (book_id, author_id)
);

CREATE TABLE book_copy (
    id       UUID         DEFAULT gen_random_uuid() PRIMARY KEY,
    book_id  UUID         NOT NULL REFERENCES book(id) ON DELETE CASCADE,
    type     VARCHAR(255) NOT NULL CHECK (type IN ('PAPERBACK', 'HARDBACK', 'POCKET')),
    location VARCHAR(10)  NOT NULL,
    UNIQUE (book_id, type)
);

CREATE TABLE book_copy_prices (
    id            UUID              DEFAULT gen_random_uuid() PRIMARY KEY,
    book_copy_id  UUID              NOT NULL REFERENCES book_copy(id) ON DELETE CASCADE,
    date          TIMESTAMP         NOT NULL,
    price         DECIMAL(10, 2)    NOT NULL
);

CREATE TABLE sale (
    id                    UUID         DEFAULT gen_random_uuid() PRIMARY KEY,
    status                VARCHAR(255) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'CONFIRMED', 'REJECTED')),
    is_reservation        BOOLEAN      NOT NULL DEFAULT FALSE,
    creation_date_time     TIMESTAMP    NOT NULL DEFAULT NOW(),
    finalization_date_time TIMESTAMP
);

CREATE TABLE sale_book_copy (
    id            UUID    DEFAULT gen_random_uuid() PRIMARY KEY,
    sale_id       UUID    NOT NULL REFERENCES sale(id)     ON DELETE CASCADE,
    book_copy_id  UUID    NOT NULL REFERENCES book_copy(id),
    quantity      INTEGER NOT NULL CHECK (quantity > 0)
);

CREATE TABLE arrival (
    id                 UUID      DEFAULT gen_random_uuid() PRIMARY KEY,
    arrival_date_time  TIMESTAMP NOT NULL
);

CREATE TABLE arrival_book (
    id            UUID    DEFAULT gen_random_uuid() PRIMARY KEY,
    arrival_id    UUID    NOT NULL REFERENCES arrival(id)   ON DELETE CASCADE,
    book_copy_id  UUID    NOT NULL REFERENCES book_copy(id) ON DELETE CASCADE,
    quantity      INTEGER NOT NULL
);
