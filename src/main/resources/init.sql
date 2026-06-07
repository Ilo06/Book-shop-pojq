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
    sale_id         UUID           NOT NULL REFERENCES sale(id),
    book_copy_id    UUID           NOT NULL REFERENCES book_copy(id),
    price           DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (sale_id, book_copy_id)
);

CREATE TABLE reservation (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    reservation_date TIMESTAMP NOT NULL,
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

-- ============================================
-- DUMMY DATA
-- ============================================

-- Genres
INSERT INTO genre (id, name) VALUES
    ('a1b2c3d4-1111-4000-8000-000000000001', 'Science-Fiction'),
    ('a1b2c3d4-1111-4000-8000-000000000002', 'Fantasy'),
    ('a1b2c3d4-1111-4000-8000-000000000003', 'Thriller'),
    ('a1b2c3d4-1111-4000-8000-000000000004', 'Romance'),
    ('a1b2c3d4-1111-4000-8000-000000000005', 'Biography'),
    ('a1b2c3d4-1111-4000-8000-000000000006', 'Programming');

-- Authors
INSERT INTO author (id, first_name, last_name) VALUES
    ('b2c3d4e5-2222-4000-8000-000000000001', 'George', 'Orwell'),
    ('b2c3d4e5-2222-4000-8000-000000000002', 'J.K.', 'Rowling'),
    ('b2c3d4e5-2222-4000-8000-000000000003', 'Agatha', 'Christie'),
    ('b2c3d4e5-2222-4000-8000-000000000004', 'Jane', 'Austen'),
    ('b2c3d4e5-2222-4000-8000-000000000005', 'Robert', 'Martin'),
    ('b2c3d4e5-2222-4000-8000-000000000006', 'Frank', 'Herbert');

-- Books
INSERT INTO book (id, title, isbn, description, publish_date, genre_id) VALUES
    ('c3d4e5f6-3333-4000-8000-000000000001', '1984',                    '9780451524935', 'A dystopian novel set in a totalitarian society ruled by Big Brother.',                              '1949-06-08', 'a1b2c3d4-1111-4000-8000-000000000001'),
    ('c3d4e5f6-3333-4000-8000-000000000002', 'Harry Potter and the Philosopher''s Stone', '9780747532699', 'A young wizard discovers his magical heritage and attends Hogwarts.',                          '1997-06-26', 'a1b2c3d4-1111-4000-8000-000000000002'),
    ('c3d4e5f6-3333-4000-8000-000000000003', 'Murder on the Orient Express', '9780062693662', 'Detective Hercule Poirot investigates a murder on a stalled train.',                            '1934-01-01', 'a1b2c3d4-1111-4000-8000-000000000003'),
    ('c3d4e5f6-3333-4000-8000-000000000004', 'Pride and Prejudice',     '9780141439518', 'Elizabeth Bennet navigates issues of manners, morality, and marriage in 19th-century England.',  '1813-01-28', 'a1b2c3d4-1111-4000-8000-000000000004'),
    ('c3d4e5f6-3333-4000-8000-000000000005', 'Clean Code',              '9780132350884', 'A handbook of agile software craftsmanship and best practices for writing clean code.',          '2008-08-01', 'a1b2c3d4-1111-4000-8000-000000000006'),
    ('c3d4e5f6-3333-4000-8000-000000000006', 'Dune',                    '9780441013593', 'Set on the desert planet Arrakis, a story of politics, religion, and the spice melange.',         '1965-08-01', 'a1b2c3d4-1111-4000-8000-000000000001'),
    ('c3d4e5f6-3333-4000-8000-000000000007', 'The Hobbit',              '9780547928227', 'Bilbo Baggins embarks on an unexpected journey with a company of dwarves.',                      '1937-09-21', 'a1b2c3d4-1111-4000-8000-000000000002'),
    ('c3d4e5f6-3333-4000-8000-000000000008', 'And Then There Were None', '9780062073488', 'Ten strangers lured to an island are murdered one by one.',                                      '1939-11-06', 'a1b2c3d4-1111-4000-8000-000000000003');

-- Book-Author relationships
INSERT INTO book_author (book_id, author_id) VALUES
    ('c3d4e5f6-3333-4000-8000-000000000001', 'b2c3d4e5-2222-4000-8000-000000000001'),
    ('c3d4e5f6-3333-4000-8000-000000000002', 'b2c3d4e5-2222-4000-8000-000000000002'),
    ('c3d4e5f6-3333-4000-8000-000000000003', 'b2c3d4e5-2222-4000-8000-000000000003'),
    ('c3d4e5f6-3333-4000-8000-000000000004', 'b2c3d4e5-2222-4000-8000-000000000004'),
    ('c3d4e5f6-3333-4000-8000-000000000005', 'b2c3d4e5-2222-4000-8000-000000000005'),
    ('c3d4e5f6-3333-4000-8000-000000000006', 'b2c3d4e5-2222-4000-8000-000000000006'),
    ('c3d4e5f6-3333-4000-8000-000000000007', 'b2c3d4e5-2222-4000-8000-000000000002'),
    ('c3d4e5f6-3333-4000-8000-000000000008', 'b2c3d4e5-2222-4000-8000-000000000003');

-- Book Copies (some available, some sold, some reserved)
INSERT INTO book_copy (id, book_id, status, acquisition_date, location) VALUES
    -- 1984 copies
    ('d4e5f6a7-4444-4000-8000-000000000001', 'c3d4e5f6-3333-4000-8000-000000000001', 'AVAILABLE', '2024-01-15', 'Shelf A1'),
    ('d4e5f6a7-4444-4000-8000-000000000002', 'c3d4e5f6-3333-4000-8000-000000000001', 'SOLD',      '2024-01-15', 'Shelf A1'),
    ('d4e5f6a7-4444-4000-8000-000000000003', 'c3d4e5f6-3333-4000-8000-000000000001', 'SOLD',      '2024-03-10', 'Shelf A1'),
    -- Harry Potter copies
    ('d4e5f6a7-4444-4000-8000-000000000004', 'c3d4e5f6-3333-4000-8000-000000000002', 'AVAILABLE', '2024-02-01', 'Shelf B2'),
    ('d4e5f6a7-4444-4000-8000-000000000005', 'c3d4e5f6-3333-4000-8000-000000000002', 'AVAILABLE', '2024-02-01', 'Shelf B2'),
    ('d4e5f6a7-4444-4000-8000-000000000006', 'c3d4e5f6-3333-4000-8000-000000000002', 'RESERVED',  '2024-06-01', 'Shelf B2'),
    -- Murder on the Orient Express copies
    ('d4e5f6a7-4444-4000-8000-000000000007', 'c3d4e5f6-3333-4000-8000-000000000003', 'AVAILABLE', '2024-04-20', 'Shelf C3'),
    ('d4e5f6a7-4444-4000-8000-000000000008', 'c3d4e5f6-3333-4000-8000-000000000003', 'SOLD',      '2024-05-15', 'Shelf C3'),
    -- Pride and Prejudice copies
    ('d4e5f6a7-4444-4000-8000-000000000009', 'c3d4e5f6-3333-4000-8000-000000000004', 'AVAILABLE', '2024-03-01', 'Shelf D4'),
    ('d4e5f6a7-4444-4000-8000-000000000010', 'c3d4e5f6-3333-4000-8000-000000000004', 'SOLD',      '2024-03-01', 'Shelf D4'),
    -- Clean Code copies
    ('d4e5f6a7-4444-4000-8000-000000000011', 'c3d4e5f6-3333-4000-8000-000000000005', 'AVAILABLE', '2024-07-10', 'Shelf E5'),
    ('d4e5f6a7-4444-4000-8000-000000000012', 'c3d4e5f6-3333-4000-8000-000000000005', 'AVAILABLE', '2024-07-10', 'Shelf E5'),
    ('d4e5f6a7-4444-4000-8000-000000000013', 'c3d4e5f6-3333-4000-8000-000000000005', 'SOLD',      '2024-08-01', 'Shelf E5'),
    -- Dune copies
    ('d4e5f6a7-4444-4000-8000-000000000014', 'c3d4e5f6-3333-4000-8000-000000000006', 'AVAILABLE', '2024-09-01', 'Shelf F6'),
    ('d4e5f6a7-4444-4000-8000-000000000015', 'c3d4e5f6-3333-4000-8000-000000000006', 'SOLD',      '2024-09-01', 'Shelf F6'),
    ('d4e5f6a7-4444-4000-8000-000000000016', 'c3d4e5f6-3333-4000-8000-000000000006', 'AVAILABLE', '2025-01-10', 'Shelf F6'),
    -- The Hobbit copies
    ('d4e5f6a7-4444-4000-8000-000000000017', 'c3d4e5f6-3333-4000-8000-000000000007', 'AVAILABLE', '2024-11-20', 'Shelf G7'),
    -- And Then There Were None copies
    ('d4e5f6a7-4444-4000-8000-000000000018', 'c3d4e5f6-3333-4000-8000-000000000008', 'AVAILABLE', '2025-02-15', 'Shelf H8');

-- Sales
INSERT INTO sale (id, sale_date) VALUES
    ('e5f6a7b8-5555-4000-8000-000000000001', '2024-06-15'),
    ('e5f6a7b8-5555-4000-8000-000000000002', '2024-07-20'),
    ('e5f6a7b8-5555-4000-8000-000000000003', '2025-01-10'),
    ('e5f6a7b8-5555-4000-8000-000000000004', '2025-03-05');

-- Sale Book Copies (which book copies were sold in which sale)
INSERT INTO sale_book_copy (sale_id, book_copy_id, price) VALUES
    ('e5f6a7b8-5555-4000-8000-000000000001', 'd4e5f6a7-4444-4000-8000-000000000002', 12.99),
    ('e5f6a7b8-5555-4000-8000-000000000001', 'd4e5f6a7-4444-4000-8000-000000000007', 14.50),
    ('e5f6a7b8-5555-4000-8000-000000000002', 'd4e5f6a7-4444-4000-8000-000000000008', 11.99),
    ('e5f6a7b8-5555-4000-8000-000000000003', 'd4e5f6a7-4444-4000-8000-000000000003', 13.99),
    ('e5f6a7b8-5555-4000-8000-000000000003', 'd4e5f6a7-4444-4000-8000-000000000015', 18.00),
    ('e5f6a7b8-5555-4000-8000-000000000004', 'd4e5f6a7-4444-4000-8000-000000000010', 9.99);

-- Reservations
INSERT INTO reservation (id, reservation_date, status) VALUES
    ('f6a7b8c9-6666-4000-8000-000000000001', '2025-05-01 10:30:00', 'PENDING'),
    ('f6a7b8c9-6666-4000-8000-000000000002', '2025-05-10 14:00:00', 'CONFIRMED'),
    ('f6a7b8c9-6666-4000-8000-000000000003', '2025-05-15 09:15:00', 'CANCELLED'),
    ('f6a7b8c9-6666-4000-8000-000000000004', '2025-05-20 16:45:00', 'PENDING');

-- Reservation Books
INSERT INTO reservation_book (reservation_id, book_id, quantity) VALUES
    ('f6a7b8c9-6666-4000-8000-000000000001', 'c3d4e5f6-3333-4000-8000-000000000002', 2),
    ('f6a7b8c9-6666-4000-8000-000000000002', 'c3d4e5f6-3333-4000-8000-000000000006', 1),
    ('f6a7b8c9-6666-4000-8000-000000000003', 'c3d4e5f6-3333-4000-8000-000000000005', 1),
    ('f6a7b8c9-6666-4000-8000-000000000004', 'c3d4e5f6-3333-4000-8000-000000000001', 1),
    ('f6a7b8c9-6666-4000-8000-000000000004', 'c3d4e5f6-3333-4000-8000-000000000007', 1);

-- Arrivals
INSERT INTO arrival (id, arrival_date) VALUES
    ('a7b8c9d0-7777-4000-8000-000000000001', '2024-06-01'),
    ('a7b8c9d0-7777-4000-8000-000000000002', '2024-09-15'),
    ('a7b8c9d0-7777-4000-8000-000000000003', '2025-02-01');

-- Arrival Books (which books arrived and in what quantity)
INSERT INTO arrival_book (arrival_id, book_id, quantity) VALUES
    ('a7b8c9d0-7777-4000-8000-000000000001', 'c3d4e5f6-3333-4000-8000-000000000001', 3),
    ('a7b8c9d0-7777-4000-8000-000000000001', 'c3d4e5f6-3333-4000-8000-000000000002', 3),
    ('a7b8c9d0-7777-4000-8000-000000000001', 'c3d4e5f6-3333-4000-8000-000000000003', 2),
    ('a7b8c9d0-7777-4000-8000-000000000002', 'c3d4e5f6-3333-4000-8000-000000000004', 2),
    ('a7b8c9d0-7777-4000-8000-000000000002', 'c3d4e5f6-3333-4000-8000-000000000006', 3),
    ('a7b8c9d0-7777-4000-8000-000000000003', 'c3d4e5f6-3333-4000-8000-000000000005', 3),
    ('a7b8c9d0-7777-4000-8000-000000000003', 'c3d4e5f6-3333-4000-8000-000000000007', 1),
    ('a7b8c9d0-7777-4000-8000-000000000003', 'c3d4e5f6-3333-4000-8000-000000000008', 1);
