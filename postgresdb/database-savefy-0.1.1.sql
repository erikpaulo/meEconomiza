/**
* heroku pg:psql postgresql-convex-43609 --app savefy-staging < /Users/eriklacerda/Dev-Projects/savefy/postgresdb/database-savefy-0.1.1.sql
* Localhost  -- \i /Users/eriklacerda/Dev-Projects/savefy/postgresdb/database-savefy-0.1.1.sql
*/

ALTER TABLE ACCOUNT ADD COLUMN BENEFIT_DESCRIPTION VARCHAR(250);