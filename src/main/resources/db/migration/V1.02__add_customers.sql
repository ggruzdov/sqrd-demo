-- Add customers with BCrypt-hashed passwords
-- Password for John Doe: password123
INSERT INTO customers (first_name, last_name, phone, password)
VALUES ('John', 'Doe', '5551234567', '$2a$10$98xGZ.nCuu2rjRnDHflItualEvG3Bz2gNJ1ao0JXJxHDQ.QxtbG/6');

-- Password for Jane Smith: securePass
INSERT INTO customers (first_name, last_name, phone, password)
VALUES ('Jane', 'Smith', '5559876543', '$2a$10$IDrFkgt25.lJf/VHPYrJOu4K35SQBrrYe2msJ8m2CkewBKbroUxUu');

-- Password for Mike Johnson: mike1234
INSERT INTO customers (first_name, last_name, phone, password)
VALUES ('Mike', 'Johnson', '5552223333', '$2a$10$Q7pzvHZpAplhT.rASGi0juyZpfYdww2QA863F96qnfeWvi7JZyUQS');