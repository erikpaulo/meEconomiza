/**
* heroku pg:psql --app meeconomiza < /Users/eriklacerda/Documents/Personal/Projetos/meEconomia/src/main/resources/config/postgres/db-postgres-clear.sql
*/

DELETE FROM USER_ROLE;
DELETE FROM USER_ACCOUNT;
DELETE FROM USER_GROUP;