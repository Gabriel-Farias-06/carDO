--liquibase formatted sql
--changeset gabriel:202504261242
--comment: block table create

create table block (
    idblock varchar(200) primary key,
    title varchar(255) not null unique,
    blocked_at timestamp default current_timestamp,
    block_reason varchar(255) not null,
    unblocked_at timestamp null,
    unblock_reason varchar(255) not null,
    fk_card varchar(200) not null,
    constraint foreign key (fk_card) references card(idcard) on delete cascade
) ENGINE=InnoDB;

--rollback drop table block