/**
* Changeset 2017.01
* Criação da aplicação.
* heroku pg:psql --app savefy < /Users/eriklacerda/Projects/savefy/src/main/resources/config/postgres/db-postgres-savefy-2017.01.sql
* Localhost  -- \i /Users/eriklacerda/Dev-Projects/savefy/src/main/resources/config/postgres/data/db-postgres-savefy-2017.01.sql
*/

CREATE DATABASE savefy;

CREATE USER eriklacerda;
ALTER ROLE eriklacerda WITH LOGIN;

\connect savefy;

--DROP TABLE IF EXISTS user_role;
--DROP TABLE IF EXISTS user_account;
-- DROP TABLE IF EXISTS USER_GROUP;
-- DROP TABLE IF EXISTS remember_me_token;
 DROP TABLE IF EXISTS CONCILIATION_ENTRY;
 DROP TABLE IF EXISTS CONCILIATION;
 DROP TABLE IF EXISTS ACCOUNT_ENTRY;
 DROP TABLE IF EXISTS ACCOUNT;
-- DROP TABLE IF EXISTS SUBCATEGORY;
-- DROP TABLE IF EXISTS CATEGORY;
-- DROP TABLE IF EXISTS CATEGORY_PREDICTION;
-- DROP TABLE IF EXISTS SANITIZE_PATTERN;
-- DROP TABLE IF EXISTS USER_PREFERENCES;


CREATE TABLE remember_me_token (
	id 			SERIAL PRIMARY KEY,
	date 		TIMESTAMP,
	series 		VARCHAR(255),
	token_value VARCHAR(255),
	username 	VARCHAR(255)
);

CREATE TABLE USER_GROUP (
	ID   SERIAL PRIMARY KEY,
	NAME VARCHAR(255)
);

CREATE TABLE user_account (
	id 				SERIAL PRIMARY KEY,
	account_locked 	BOOLEAN,
	display_name 	VARCHAR(255),
	email 			VARCHAR(255) UNIQUE,
	image_url 		VARCHAR(255),
	password 		VARCHAR(64),
	trusted_account BOOLEAN,
	google_id       VARCHAR(255) UNIQUE,
	web_site 		VARCHAR(255),
	GROUP_ID        INTEGER REFERENCES USER_GROUP(ID)
);

CREATE TABLE user_role (
	user_id INTEGER REFERENCES user_account(id),
	role    VARCHAR(255),
	PRIMARY KEY (user_id, role)
);

CREATE TABLE CATEGORY (
	ID      SERIAL PRIMARY KEY,
	NAME    VARCHAR(50),
	TYPE    VARCHAR(3), --EXP - expenses, INC - incomes, INV - investiments
	USER_GROUP_ID INTEGER REFERENCES USER_GROUP(id),

	CONSTRAINT U_CONST_01 UNIQUE (USER_GROUP_ID,NAME,TYPE)
);

CREATE TABLE SUBCATEGORY (
	ID             SERIAL PRIMARY KEY,
	NAME           VARCHAR(50),
	ACTIVATED      BOOLEAN NOT NULL,
	TYPE           VARCHAR(1), --F - fixed, I - irregular, V - variable
	CATEGORY_ID    INTEGER REFERENCES CATEGORY(ID),
	USER_GROUP_ID  INTEGER NOT NULL REFERENCES USER_GROUP(id),

	CONSTRAINT U_CONST_02 UNIQUE (USER_GROUP_ID,CATEGORY_ID,NAME)
);

CREATE TABLE CATEGORY_PREDICTION (
	ID                  SERIAL PRIMARY KEY,
	DESCRIPTION         VARCHAR(50) NOT NULL,
	SUBCATEGORY_ID      INTEGER NOT NULL REFERENCES SUBCATEGORY(ID),
	TIMES_USED          INTEGER NOT NULL DEFAULT 0,
	TIMES_REJECTED         INTEGER NOT NULL DEFAULT 0,
	USER_GROUP_ID       INTEGER NOT NULL REFERENCES USER_GROUP(id)
);

CREATE TABLE SANITIZE_PATTERN (
	ID                  SERIAL PRIMARY KEY,
	PATTERN             VARCHAR(50) NOT NULL,
	REPLACE_FOR         VARCHAR(50) NOT NULL
);

CREATE TABLE INSTITUTION (
    ID              SERIAL PRIMARY KEY,
    NAME            VARCHAR(50) NOT NULL
);

CREATE TABLE ACCOUNT (
	ID             SERIAL PRIMARY KEY,
	NAME           VARCHAR(50),
	INSTITUTION    INTEGER NOT NULL REFERENCES INSTITUTION(id),
	TYPE           VARCHAR(3), --CKA - checking account, SVA - saving account, INV - investment, CCA - credit card account
	ACTIVATED      BOOLEAN NOT NULL,
	START_BALANCE  DECIMAL NOT NULL,
	LAST_UPDATE    TIMESTAMP NOT NULL,
	USER_GROUP_ID  INTEGER NOT NULL REFERENCES USER_GROUP(id)

);

CREATE TABLE ACCOUNT_ENTRY (
	ID                  SERIAL PRIMARY KEY,
	DATE                TIMESTAMP NOT NULL,
	AMOUNT              DECIMAL NOT NULL,
	ACCOUNT_ID          INTEGER NOT NULL REFERENCES ACCOUNT(ID),
	ACCOUNT_DESTINY_ID  INTEGER REFERENCES ACCOUNT(ID),
--	TWIN_ENTRY_ID       INTEGER,
	TRANSFER            BOOLEAN NOT NULL,
	SUBCATEGORY_ID      INTEGER NOT NULL REFERENCES SUBCATEGORY(ID),
	USER_GROUP_ID       INTEGER NOT NULL REFERENCES USER_GROUP(id)
);

CREATE TABLE CONCILIATION (
	ID                  SERIAL PRIMARY KEY,
	DATE                TIMESTAMP NOT NULL,
	ACCOUNT_ID          INTEGER NOT NULL REFERENCES ACCOUNT(ID),
	IMPORTED            BOOLEAN NOT NULL,
	USER_GROUP_ID       INTEGER NOT NULL REFERENCES USER_GROUP(id)
);

CREATE TABLE CONCILIATION_ENTRY (
	ID                  SERIAL PRIMARY KEY,
	DATE                TIMESTAMP NOT NULL,
	DESCRIPTION         VARCHAR(50) NOT NULL,
	SUBCATEGORY_ID      INTEGER REFERENCES SUBCATEGORY(ID),
	ACCOUNT_ENTRY_ID    INTEGER REFERENCES ACCOUNT_ENTRY(ID),
	AMOUNT              DECIMAL NOT NULL,
	REJECT              BOOLEAN NOT NULL,
	CONCILIATION_ID     INTEGER REFERENCES CONCILIATION(ID),
	USER_GROUP_ID       INTEGER NOT NULL REFERENCES USER_GROUP(id)
);

CREATE TABLE USER_PREFERENCES (
    ID                          SERIAL PRIMARY KEY,
    UPDATE_INSTALLMENT_DATE     BOOLEAN,
    USER_GROUP_ID               INTEGER NOT NULL REFERENCES USER_GROUP(id),

    CONSTRAINT U_CONST_04 UNIQUE (USER_GROUP_ID)
);



-- Initial load for user and roles

insert into user_group values (1, 'Lacerda&Moreira');
insert into user_group values (2, 'Lacerda Young');

insert into user_account values(1,false,'Erik Lacerda','erik.lacerda@gmail.com','resources/images/avatar-male.png','admin',true,'erik.lacerda@gmail.com',null,1);
insert into user_account values(2,false,'Carolina Moreira','carolinalle.paula@gmail.com','resources/images/avatar-female.png','user',true,'carolinalle.paula@gmail.com',null,1);
insert into user_account values(3,false,'Marcus Lacerda','marcus.lacerda@gmail.com','resources/images/avatar-male.png','paraiba',true,'marcus.lacerda@gmail.com',null,2);

insert into user_role values(1, 'ROLE_ADMIN');
insert into user_role values(2, 'ROLE_USER');
insert into user_role values(3, 'ROLE_ADMIN');

--Temporário até termos as inserções de conta e categoria
insert into category values (1, 'Transporte', 'EXP', 1);
insert into category values (2, 'Salário', 'INC', 1);
insert into subcategory values (1, 'Uber/Taxi/Cabify', TRUE, 'F', 1, 1);
insert into subcategory values (2, 'Combustível', TRUE, 'F', 1, 1);
insert into subcategory values (3, 'Estacionamento', TRUE, 'F', 1, 1);
insert into subcategory values (4, 'Carol', TRUE, 'F', 2, 1);
insert into account values (1, 'CC Personnalité', 'Itaú', 'P', 'CKA', TRUE, '1000', '2017-04-09', 1);
insert into account values (2, 'Visa Person', 'Itaú', 'N', 'CCA', TRUE, '0', '2017-04-09', 1);
--insert into account_entry values (1, '2017-11-12', 283.54, 1, NULL, FALSE, 1, 1);
--insert into conciliation values (1, '2017-11-13', 1, TRUE, 1);
--insert into conciliation values (2, '2017-11-15', 1, FALSE, 1);
--insert into conciliation values (3, '2017-11-13', 1, TRUE, 1);
--insert into conciliation values (4, '2017-11-15', 1, FALSE, 1);
--insert into conciliation_entry values (1, '2017-10-08', 'Uber', 1, 234.98, 1, 1);
--insert into conciliation_entry values (2, '2017-10-18', 'Combustível', 1, 1234.98, 1, 1);
--insert into conciliation_entry values (3, '2017-10-08', 'Uber', 1, 234.98, 1, 1);
--insert into conciliation_entry values (4, '2017-10-18', 'Combustível', 1, 1234.98, 1, 1);
--insert into category_prediction values (1, 'TED 237.3807CAROLINA M P', 4, 1, 0, 1);
INSERT INTO SANITIZE_PATTERN VALUES (1, '\d{2}\/\d{2}', '');
INSERT INTO SANITIZE_PATTERN VALUES (2, 'Uber UBER.*', 'Uber UBER');




GRANT ALL PRIVILEGES ON DATABASE savefy TO eriklacerda;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO eriklacerda;
