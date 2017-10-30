/**
* heroku pg:psql --app meeconomiza < /Users/eriklacerda/Documents/Personal/Projetos/meEconomiza/src/main/resources/config/postgres/db-postgres-dropall.sql
*/

DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS remember_me_token;
DROP TABLE IF EXISTS user_account;