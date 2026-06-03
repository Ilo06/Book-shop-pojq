CREATE TYPE book_status AS ENUM ('AVAILABLE', 'SOLD', 'RESERVED');
CREATE TYPE reservation_status AS ENUM ('PENDING', 'CONFIRMED', 'CANCELLED');

CREATE TABLE author (
    id          UUID            DEFAULT gen_random_uuid() PRIMARY KEY,
    first_name  VARCHAR(255)    NOT NULL,
    last_name   VARCHAR(255)    NOT NULL
);

CREATE TABLE genre (
    id      UUID            DEFAULT gen_random_uuid() PRIMARY KEY,
    name    VARCHAR(255)    NOT NULL
);

CREATE TABLE book (
    id              UUID            DEFAULT gen_random_uuid() PRIMARY KEY,
    title           VARCHAR(255)    NOT NULL,
    isbn            VARCHAR(13)     NOT NULL UNIQUE,
    description     TEXT,
    publish_date    DATE            NOT NULL,
    genre_id        UUID            NOT NULL REFERENCES genre(id)
);

CREATE TABLE book_author (
    book_id     UUID NOT NULL REFERENCES book(id),
    author_id   UUID NOT NULL REFERENCES author(id),
    PRIMARY KEY (book_id, author_id)
);

CREATE TABLE book_copy (
    id                  UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    book_id             UUID NOT NULL REFERENCES book(id),
    status              book_status NOT NULL,
    acquisition_date    DATE,
    location            VARCHAR(255)
);

CREATE TABLE sale (
    id          UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    sale_date   DATE NOT NULL
);

CREATE TABLE sale_book_copy (
    sale_id         UUID   NOT NULL REFERENCES sale(id),
    book_copy_id    UUID   NOT NULL REFERENCES book_copy(id),
    quantity        INTEGER  NOT NULL,
    unit_price      DECIMAL(10, 2)  NOT NULL,
    PRIMARY KEY (sale_id, book_copy_id)
);

CREATE TABLE reservation (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    reservation_datetime TIMESTAMP NOT NULL,
    status reservation_status NOT NULL DEFAULT 'PENDING'
);

CREATE TABLE reservation_book (
    reservation_id  UUID NOT NULL REFERENCES reservation(id),
    book_id         UUID NOT NULL REFERENCES book(id),
    quantity        INTEGER NOT NULL,
    PRIMARY KEY (reservation_id, book_id)
);

CREATE TABLE arrival (
    id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    arrival_date    DATE NOT NULL
);

CREATE TABLE arrival_book (
    arrival_id  UUID NOT NULL REFERENCES arrival(id),
    book_id     UUID NOT NULL REFERENCES book(id),
    quantity    INTEGER NOT NULL,
    PRIMARY KEY (arrival_id, book_id)
);
