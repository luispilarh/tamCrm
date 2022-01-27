create schema crmSchema;
create table "user"
(
    id    serial
        constraint user_pk
            primary key,
    login varchar               not null,
    admin boolean default false not null
);

alter table "user"
    owner to postgres;

create unique index user_login_uindex
    on "user" (login);

create table customer
(
    id      serial
        constraint customer_pk
            primary key,
    name    varchar not null,
    surname varchar,
    photo   bytea
);

alter table customer
    owner to postgres;
