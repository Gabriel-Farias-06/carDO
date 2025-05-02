--liquibase formatted sql
--changeset gabriel:202504261242
--comment: board_column table create

create table board_column (
    idboard_column varchar(200) primary key,
    name varchar(255) not null,
    `order` int not null,
    kind varchar(7) not null,
    fk_board varchar(200) not null,
    constraint foreign key (fk_board) references board(idboard) on delete cascade,
    constraint id_order_unique unique key unique_id_order (fk_board, `order`)
) ENGINE=InnoDB;

--rollback drop table board_column