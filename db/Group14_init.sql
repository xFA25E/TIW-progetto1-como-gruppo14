DROP TRIGGER IF EXISTS delete_customer;
DROP TRIGGER IF EXISTS delete_account;

DROP VIEW IF EXISTS account_total_amount;

DROP TABLE IF EXISTS transfer;
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS customer;

CREATE TABLE customer (
  customer_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  full_name VARCHAR(60) NOT NULL CHECK(LENGTH(`full_name`) <> 0),
  user_name VARCHAR(30) NOT NULL UNIQUE CHECK(LENGTH(`user_name`) <> 0),
  password_hash VARCHAR(255) NOT NULL CHECK(LENGTH(`password_hash`) <> 0),
  password_salt CHAR(30) NOT NULL CHECK(LENGTH(`password_salt`) <> 0),
  PRIMARY KEY (`customer_id`)
);

CREATE TABLE account (
  account_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  customer_id INT UNSIGNED NOT NULL,
  deposited_amount BIGINT NOT NULL DEFAULT 0,
  cause TEXT NOT NULL DEFAULT '',
  PRIMARY KEY (`account_id`),
  FOREIGN KEY (`customer_id`)
  REFERENCES customer(customer_id)
  ON UPDATE CASCADE
  ON DELETE CASCADE
);

CREATE TABLE transfer (
  transfer_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  source_account_id INT UNSIGNED NULL,
  destination_account_id INT UNSIGNED NULL,
  creation_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  amount BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`transfer_id`),
  FOREIGN KEY (`source_account_id`)
  REFERENCES account(account_id)
  ON UPDATE CASCADE
  ON DELETE SET NULL,
  FOREIGN KEY (`destination_account_id`)
  REFERENCES account(account_id)
  ON UPDATE CASCADE
  ON DELETE SET NULL
);

CREATE VIEW account_total_amount AS
  SELECT account_id,
         (
           deposited_amount
           + (
             SELECT COALESCE(SUM(amount), 0)
               FROM transfer
              WHERE destination_account_id = account_id
           )
           - (
             SELECT COALESCE(SUM(amount), 0)
               FROM transfer
              WHERE source_account_id = account_id
           )
         ) AS total_amount
    FROM account;

CREATE TRIGGER delete_customer
  AFTER DELETE ON customer
  FOR EACH ROW
    DELETE FROM transfer
    WHERE source_account_id IS NULL
    AND destination_account_id IS NULL;

CREATE TRIGGER delete_account
  AFTER DELETE ON account
  FOR EACH ROW
    DELETE FROM transfer
    WHERE source_account_id IS NULL
    AND destination_account_id IS NULL;
