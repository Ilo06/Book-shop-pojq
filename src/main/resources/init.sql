CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TYPE book_copy_status AS ENUM ('AVAILABLE', 'SOLD', 'RESERVED');
CREATE TYPE reservation_status AS ENUM ('PENDING', 'CONFIRMED', 'CANCELLED');

CREATE TABLE genre (
    id     UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name   VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE author (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name  VARCHAR(100) NOT NULL,
    last_name   VARCHAR(100) NOT NULL
);

CREATE TABLE book (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    title           VARCHAR(255) NOT NULL,
    price           NUMERIC(10, 2),
    isbn            VARCHAR(13)  NOT NULL UNIQUE CHECK(length(isbn) IN (10, 13)),
    description     TEXT,
    publish_date    DATE,
    genre_id        UUID         NOT NULL REFERENCES genre(id) ON DELETE RESTRICT
);

CREATE TABLE book_author (
    book_id     UUID NOT NULL REFERENCES book(id)   ON DELETE CASCADE,
    author_id   UUID NOT NULL REFERENCES author(id) ON DELETE CASCADE,
    PRIMARY KEY (book_id, author_id)
);

CREATE TABLE book_copy (
    id               UUID             PRIMARY KEY DEFAULT gen_random_uuid(),
    book_id          UUID             NOT NULL REFERENCES book(id) ON DELETE RESTRICT,
    status           book_copy_status NOT NULL DEFAULT 'AVAILABLE',
    location         VARCHAR(100)
);

CREATE TABLE arrival (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    date         DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE arrival_book (
    arrival_id  UUID NOT NULL REFERENCES arrival(id) ON DELETE CASCADE,
    book_id     UUID NOT NULL REFERENCES book(id)    ON DELETE RESTRICT,
    quantity    INT  NOT NULL CHECK (quantity > 0),
    PRIMARY KEY (arrival_id, book_id)
);

CREATE TABLE sale (
    id           UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    date         DATE           NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE sale_book_copy (
    sale_id      UUID NOT NULL REFERENCES sale(id)      ON DELETE CASCADE,
    book_copy_id UUID NOT NULL REFERENCES book_copy(id) ON DELETE RESTRICT,
    quantity     INT  NOT NULL CHECK (quantity > 0),
    PRIMARY KEY (sale_id, book_copy_id)
);

CREATE TABLE reservation (
    id               UUID               PRIMARY KEY DEFAULT gen_random_uuid(),
    date             DATE               NOT NULL DEFAULT CURRENT_DATE,
    status           reservation_status NOT NULL DEFAULT 'PENDING'
);

CREATE TABLE reservation_book (
    reservation_id UUID NOT NULL REFERENCES reservation(id) ON DELETE CASCADE,
    book_id        UUID NOT NULL REFERENCES book(id)        ON DELETE RESTRICT,
    quantity       INT  NOT NULL DEFAULT 1 CHECK (quantity > 0)
    PRIMARY KEY (reservation_id, book_id)
);

CREATE INDEX idx_book_genre       ON book(genre_id);
CREATE INDEX idx_book_copy_book   ON book_copy(book_id);
CREATE INDEX idx_book_copy_status ON book_copy(status);
CREATE INDEX idx_sale_date        ON sale(sale_date);
CREATE INDEX idx_reservation_status ON reservation(status);
CREATE INDEX idx_arrival_date     ON arrival(arrival_date);