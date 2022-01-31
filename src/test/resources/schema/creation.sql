create table crmuser
(
    id       serial
        constraint user_pk
            primary key,
    username varchar               not null,
    email    varchar,
    admin    boolean default false not null,
    deleted  boolean default false
);


create unique index user_login_uindex
    on crmuser (username);

create table customer
(
    id       serial
        constraint customer_pk
            primary key,
    name     varchar not null,
    surname  varchar,
    email    varchar,
    photo    varchar,
    userId integer
        constraint customer_crmuser_id_fk
            references crmuser,
    deleted  boolean default false
);


