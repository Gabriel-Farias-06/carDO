--liquibase formatted sql
--changeset gabriel:202504261242
--comment: board table create

create table board (
    idboard varchar(200) primary key,
    name varchar(255) not null unique
) ENGINE=InnoDB;

--rollback DROP TABLE BOARD