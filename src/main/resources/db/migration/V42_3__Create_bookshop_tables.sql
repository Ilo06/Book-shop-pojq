DO $$ BEGIN
  CREATE TYPE book_status AS ENUM ('AVAILABLE', 'SOLD_OUT');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
  CREATE TYPE book_copy_type AS ENUM ('PAPERBACK', 'HARDBACK', 'POCKET');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
  CREATE TYPE reservation_status AS ENUM ('PENDING', 'CONFIRMED', 'CANCELLED');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

-- Tables
CREATE TABLE IF NOT EXISTS author (
    id         UUID         DEFAULT gen_random_uuid() PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS genre (
    id   UUID         DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS book (
    id           UUID         DEFAULT gen_random_uuid() PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    isbn         VARCHAR(13)  NOT NULL UNIQUE CHECK (length(isbn) IN (10, 13)),
    description  TEXT,
    publish_date DATE         NOT NULL,
    genre_id     UUID         NOT NULL REFERENCES genre (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS book_author (
    book_id   UUID NOT NULL REFERENCES book (id) ON DELETE CASCADE,
    author_id UUID NOT NULL REFERENCES author (id) ON DELETE CASCADE,
    PRIMARY KEY (book_id, author_id)
);

CREATE TABLE IF NOT EXISTS book_copy (
    id       UUID           DEFAULT gen_random_uuid() PRIMARY KEY,
    book_id  UUID           NOT NULL REFERENCES book (id) ON DELETE CASCADE,
    status   book_status    NOT NULL,
    type     book_copy_type NOT NULL,
    price    DECIMAL(10, 2) NOT NULL,
    location VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS sale (
    id        UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    sale_date DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS sale_book_copy (
    sale_id      UUID NOT NULL REFERENCES sale (id) ON DELETE CASCADE,
    book_copy_id UUID NOT NULL REFERENCES book_copy (id) ON DELETE CASCADE,
    PRIMARY KEY (sale_id, book_copy_id)
);

CREATE TABLE IF NOT EXISTS reservation (
    id               UUID               DEFAULT gen_random_uuid() PRIMARY KEY,
    reservation_date TIMESTAMP          NOT NULL,
    status           reservation_status NOT NULL DEFAULT 'PENDING'
);

CREATE TABLE IF NOT EXISTS reservation_book (
    reservation_id UUID    NOT NULL REFERENCES reservation (id) ON DELETE CASCADE,
    book_copy_id   UUID    NOT NULL REFERENCES book_copy (id) ON DELETE CASCADE,
    quantity       INTEGER NOT NULL,
    PRIMARY KEY (reservation_id, book_copy_id)
);

CREATE TABLE IF NOT EXISTS arrival (
    id           UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    arrival_date DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS arrival_book (
    arrival_id   UUID    NOT NULL REFERENCES arrival (id) ON DELETE CASCADE,
    book_copy_id UUID    NOT NULL REFERENCES book_copy (id) ON DELETE CASCADE,
    quantity     INTEGER NOT NULL,
    PRIMARY KEY (arrival_id, book_copy_id)
);
