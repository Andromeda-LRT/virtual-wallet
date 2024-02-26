CREATE TABLE roles
(
    role_id   int auto_increment
        primary key,
    role_name varchar(20) not null
);

CREATE TABLE users
(
    user_id         int auto_increment
        primary key,
    username        varchar(50)          not null,
    password        varchar(50)          not null,
    first_name      varchar(50)          not null,
    last_name       varchar(50)          not null,
    email           varchar(50)          not null,
    role_id         int                not null,
    is_blocked      tinyint(1) default 0 not null,
    is_archived     tinyint(1) default 0 not null,
    phone_number    varchar(15)          not null,
    profile_picture varchar(100)         null,
    constraint users_roles_role_id_fk
        foreign key (role_id) references roles (role_id)
);

CREATE TABLE wallets
(
    wallet_id int auto_increment
        primary key,
    iban      varchar(34) not null,
    balance   long default 0,
    is_archived     tinyint(1) default 0 not null,
    user_id   int         not null,
    constraint wallets_users_user_id_fk
        foreign key (user_id) references users (user_id)

);

CREATE TABLE card_types
(
    card_type_id int auto_increment
        primary key,
    type         varchar(15) not null
);

CREATE TABLE cvv_numbers
(
    cvv_number_id int auto_increment
        primary key,
    cvv           int not null
);

CREATE TABLE cards
(
    card_id         int auto_increment
        primary key,
    number          varchar(19) not null,
    expiration_date date        not null,
    card_type_id    int         not null,
    cvv_number_id   int         not null,
    is_archived     tinyint(1) default 0 not null,
    constraint cards_card_types_card_type_id_fk
        foreign key (card_type_id) references card_types (card_type_id),
    constraint cards_cvv_numbers_cvv_number_id_fk
        foreign key (cvv_number_id) references cvv_numbers (cvv_number_id)
);


CREATE TABLE users_cards
(
    card_id int not null,
    user_id int not null,
    constraint cards_card_id_fk
        foreign key (card_id) references cards (card_id),
    constraint users_user_id_fk
        foreign key (user_id) references users (user_id)
);

CREATE TABLE transaction_types
(
    transaction_type_id int auto_increment
        primary key,
    type                varchar(15)
);

CREATE TABLE transactions
(
    transaction_id      int auto_increment
        primary key,
    amount              long     not null,
    time                datetime not null,
    transaction_type_id int      not null,
    user_id             int      not null,
    recipient_wallet_id int      not null,
    wallet_id           int      not null,
    constraint transactions_transaction_types_transaction_type_id_fk
        foreign key (transaction_type_id) references transaction_types (transaction_type_id),
    constraint transactions_users_user_id_fk
        foreign key (user_id) references users (user_id),
    constraint transactions_wallets_recipient_wallet_id_fk
        foreign key (recipient_wallet_id) references wallets (wallet_id),
    constraint transactions_wallets_wallet_id_fk
        foreign key (wallet_id) references wallets (wallet_id)
);