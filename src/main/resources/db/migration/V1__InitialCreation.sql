create table crmuser
(
    id    serial
        constraint user_pk
            primary key,
    username varchar               not null,
    email varchar,
    admin boolean default false not null
);

alter table crmuser
    owner to postgres;

create unique index user_login_uindex
    on crmuser (username);

create table customer
(
    id      serial
        constraint customer_pk
            primary key,
    name    varchar not null,
    surname varchar,
    email   varchar,
    photo   bytea
);

alter table customer
    owner to postgres;
