alter table customer
    add "userId" int;

alter table customer
    add constraint customer_crmuser_id_fk
        foreign key ("userId") references crmuser;

