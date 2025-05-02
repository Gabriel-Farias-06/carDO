--liquibase formatted sql
--changeset gabriel:202504261242
--comment: card table create

create table card (
    idcard varchar(200) primary key,
    title varchar(255) not null unique,
    description varchar(255) not null,
    fk_board_column varchar(200) not null,
    constraint foreign key (fk_board_column) references board_column(idboard_column) on delete cascade
) ENGINE=InnoDB;

--rollback drop table card