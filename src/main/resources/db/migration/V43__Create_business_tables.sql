create table if not exists author
(
    id         uuid default gen_random_uuid() primary key,
    first_name varchar(255) not null,
    last_name  varchar(255) not null
);

create table if not exists genre
(
    id   uuid default gen_random_uuid() primary key,
    name varchar(255) not null
);

create table if not exists book
(
    id           uuid default gen_random_uuid() primary key,
    title        varchar(255) not null,
    isbn         varchar(13)  not null unique,
    description  text,
    publish_date date         not null,
    genre_id     uuid         not null references genre (id)
);

create table if not exists book_author
(
    book_id   uuid not null references book (id),
    author_id uuid not null references author (id),
    primary key (book_id, author_id)
);

create table if not exists book_copy
(
    id       uuid            default gen_random_uuid() primary key,
    book_id  uuid            not null references book (id),
    status   varchar(50)     not null,
    price    decimal(10, 2) not null,
    location varchar(255)
);

create table if not exists sale
(
    id        uuid default gen_random_uuid() primary key,
    sale_date date not null
);

create table if not exists sale_book_copy
(
    sale_id      uuid            not null references sale (id),
    book_copy_id uuid            not null references book_copy (id),
    price        decimal(10, 2) not null,
    primary key (sale_id, book_copy_id)
);

create table if not exists reservation
(
    id                uuid        default gen_random_uuid() primary key,
    reservation_date  timestamp   not null,
    status            varchar(50) not null default 'PENDING'
);

create table if not exists reservation_book
(
    reservation_id uuid    not null references reservation (id),
    book_id        uuid    not null references book (id),
    quantity       integer not null,
    primary key (reservation_id, book_id)
);

create table if not exists arrival
(
    id           uuid default gen_random_uuid() primary key,
    arrival_date date not null
);

create table if not exists arrival_book
(
    arrival_id uuid    not null references arrival (id),
    book_id    uuid    not null references book (id),
    quantity   integer not null,
    primary key (arrival_id, book_id)
);
