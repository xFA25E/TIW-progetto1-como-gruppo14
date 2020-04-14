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
  cause TINYTEXT NOT NULL DEFAULT '',
  CHECK (`source_account_id` <> `destination_account_id`),
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

INSERT INTO `account` VALUES (111,41,1000000),(121,41,500000),(131,51,5000000),(141,61,2500000),(151,71,7500000),(161,71,2500000);

INSERT INTO `customer` VALUES (41,'Gregory Melendez','gregory_melendez','hE4Z5phPsV/wwQmseecDTPztiEc0C43P6E4RiKKjUJE=','c)RAv:5T/KgypDGy6.}\\3Y@b2:6En6'),(51,'Marcus Shannon','marcus_shannon','7S+LsWqMAMzrBU8kF9NvnhV/5GitX2RdIuhB5F1rDCU=','L\\2W%nCVHA$/i6OECmuODe5mx8G|DE'),(61,'Neva Kidd','neva_kidd','QvXIgmfkVXbf6NOGVE9BLhpWIpP+vye1jpDmZvGwtVM=','cY4R<94Sp(junI&B4X.%;(m\\0?8Jy.'),(71,'Weston Rasmussen','weston_rasmussen','i7RM92Cbg7TK3bxPl0jkFyRtMT6zcILHZ7/NDB0xUEE=','dUhB-L>Q~<7aM/[@rD/*9SCCze>*_B');

INSERT INTO `transfer` VALUES (1,111,131,'2020-01-11 00:00:00',50000,'restituzione del prestito di natura infruttifera'),(11,151,121,'2020-02-11 00:00:00',1500000,'regalo per acquisto auto'),(21,111,151,'2020-03-11 00:00:00',150000,'giroconto'),(31,151,111,'2020-04-14 20:18:37',42069,'Epic shopping'),(41,151,141,'2020-01-11 00:00:00',500011,'prestito (grazie)'),(51,121,151,'2020-02-11 00:00:00',50000,'Rimborso spesa viaggio \"d\'affari\" ;)'),(61,141,131,'2020-03-11 00:00:00',15000,'prestito'),(71,121,131,'2020-04-14 20:21:30',30000,'Spesa gita duemilamai'),(81,161,111,'2020-01-11 00:00:00',50223,'computer'),(91,141,151,'2020-02-11 00:00:00',200000,'rimborso spese'),(101,161,131,'2020-03-11 00:00:00',100000,'buon amici'),(111,161,121,'2020-04-14 20:24:02',144800,'party in las vegas'),(121,121,141,'2020-01-11 00:00:00',70000,'Spesa online'),(131,141,161,'2020-02-11 00:00:00',51477,'calcolatrice'),(141,131,151,'2020-03-11 00:00:00',250000,'rimborso spese'),(151,131,151,'2020-04-14 20:26:32',91311,'bottiglietta di plastica');
