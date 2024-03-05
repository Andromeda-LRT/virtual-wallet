INSERT INTO roles (role_name)
VALUES ('user'),
       ('admin');

INSERT INTO statuses(status_name)
VALUES ('Pending'),
       ('Approved'),
       ('Cancelled');

INSERT INTO users (username, password, first_name, last_name, email, role_id, is_blocked, is_archived, phone_number)
VALUES ('john_doe', 'pass123', 'John', 'Doe', 'john.doe@example.com', 2, false, false, '0875566248'),
       ('jane_smith', 'pass123', 'Jane', 'Smith', 'jane.smith@example.com', 1, false, false, '0875566248'),
       ('julia_davis', 'pass234', 'Julia', 'Davis', 'julia.davis@example.com', 1, false, false, '0872366248'),
       ('robert_miller', 'pass123', 'Robert', 'Miller', 'robert.miller@example.com', 1, false, false, '0875523548'),
       ('laura_moore', 'pass123', 'Laura', 'Moore', 'laura.moore@example.com', 1, false, false, '0875587478');

INSERT INTO wallets (iban, balance, is_archived, created_by, name)
VALUES ('NL52ABNA7650143244', 0, false, 3, 'Wallet'),
       ('BG46BNPA94405271973851', 0, false, 1, 'Wallet'),
       ('MK42539481826851967', 0, false, 2, 'Wallet'),
       ('BG91TTBB94003263799846', 0, false, 4, 'Wallet');

INSERT INTO user_wallets (user_id, wallet_id)
# VALUES (3, 1),
#        (1, 2),
#        (2, 3),
#        (4, 4);
    VALUES   (6, 5);

INSERT INTO card_types(type)
VALUES ('Credit'),
       ('Debit');

INSERT INTO cvv_numbers(cvv)
VALUES (425),
       (654),
       (894),
       (774);

INSERT INTO cards(number, expiration_date, card_type_id, card_holder, cvv_number_id, is_archived)
VALUES ('5425233430109903', '2024-02-18', 1, 'John Doe', 1, false),
       ('2222420000001113', '2024-02-18', 1, 'Jane Smith', 2, false),
       ('4917484589897107', '2024-02-18', 2, 'Laura Moore', 3, false);

# Change the date

INSERT INTO users_cards(card_id, user_id)
VALUES (1, 1),
       (2, 2),
       (3, 5);

INSERT INTO transaction_types(type)
VALUES ('Incoming'),
       ('Outgoing');


INSERT INTO wallet_transactions(amount, time, transaction_type_id, user_id, recipient_wallet_id, wallet_id, status_id)
VALUES ('3000', '2024-01-25 20:50:00', 1, 6, 2, 5, 2),
       ('4000', '2024-01-24 20:50:00', 1, 6, 1, 5, 2),
       ('5000', '2024-01-23 20:50:00', 1, 6, 3, 5, 2);