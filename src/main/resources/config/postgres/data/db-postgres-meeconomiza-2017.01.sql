/**
* Changeset 2017.01
* Criação da aplicação.
* heroku pg:psql --app meeconomiza < /Users/eriklacerda/Projects/meEconomiza/src/main/resources/config/postgres/db-postgres-meeconomiza-2017.01.sql
* Localhost  -- \i /Users/eriklacerda/Dev-Projects/meEconomiza/src/main/resources/config/postgres/db-postgres-meeconomiza-2017.01.sql
*/

DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS user_account;
DROP TABLE IF EXISTS USER_GROUP;
DROP TABLE IF EXISTS remember_me_token;

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