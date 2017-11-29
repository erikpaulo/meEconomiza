/**
* Changeset 2017.01
* Criação da aplicação.
* heroku pg:psql --app meEconomiza < /Users/eriklacerda/Projects/meEconomiza/src/main/resources/config/postgres/db-postgres-meEconomiza-2017.01.sql
* Localhost  -- \i /Users/eriklacerda/Dev-Projects/meEconomiza/src/main/resources/config/postgres/data/db-postgres-meEconomiza-2017.01.sql
*/

--DROP TABLE IF EXISTS user_role;
--DROP TABLE IF EXISTS user_account;
--DROP TABLE IF EXISTS USER_GROUP;
--DROP TABLE IF EXISTS remember_me_token;
DROP TABLE IF EXISTS CONCILIATION_ENTRY;
DROP TABLE IF EXISTS CONCILIATION;
DROP TABLE IF EXISTS ACCOUNT_ENTRY;
DROP TABLE IF EXISTS ACCOUNT;
DROP TABLE IF EXISTS SUBCATEGORY;
DROP TABLE IF EXISTS CATEGORY;
DROP TABLE IF EXISTS CATEGORY_PREDICTION;
DROP TABLE IF EXISTS SANITIZE_PATTERN;
DROP TABLE IF EXISTS USER_PREFERENCES;


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

CREATE TABLE ACCOUNT (
	ID             SERIAL PRIMARY KEY,
	NAME           VARCHAR(50),
	INSTITUTION    VARCHAR(50),
	KIND           VARCHAR(1), --P - Positive, N - Negative
	TYPE           VARCHAR(3), --CKA - checking account, SVA - saving account, INV - investment, CCA - credit card account
	ACTIVATED      BOOLEAN NOT NULL,
	START_BALANCE  DECIMAL NOT NULL,
	LAST_UPDATE    TIMESTAMP NOT NULL,
	USER_GROUP_ID  INTEGER NOT NULL REFERENCES USER_GROUP(id),

	CONSTRAINT U_CONST_03 UNIQUE (USER_GROUP_ID,NAME,TYPE/*,ACTIVATED*/)
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



--Temporário até termos as inserções de conta e categoria
INSERT INTO user_group values ()


insert into category values (1, 'Transporte', 'EXP', 1);
insert into category values (2, 'Salário', 'INC', 1);
insert into subcategory values (1, 'Uber/Taxi/Cabify', TRUE, 'F', 1, 1);
insert into subcategory values (2, 'Combustível', TRUE, 'F', 1, 1);
insert into subcategory values (3, 'Estacionamento', TRUE, 'F', 1, 1);
insert into subcategory values (4, 'Carol', TRUE, 'F', 2, 1);
insert into account values (1, 'CC Personnalité', 'Itaú', 'P', 'CKA', TRUE, '1000', '2017-04-09', 1);
insert into account values (2, 'Visa Person', 'Itaú', 'N', 'CCA', TRUE, '0', '2017-04-09', 1);
INSERT INTO SANITIZE_PATTERN VALUES (1, '\d{2}\/\d{2}', '');
INSERT INTO SANITIZE_PATTERN VALUES (2, 'Uber UBER.*', 'Uber UBER');