/**
* heroku pg:psql postgresql-convex-43609 --app savefy-staging < /Users/eriklacerda/Dev-Projects/savefy/postgresdb/database-savefy-0.1.2.sql
* Localhost  -- \i /Users/eriklacerda/Dev-Projects/savefy/postgresdb/database-savefy-0.1.2.sql
*/

ALTER TABLE ACCOUNT_ENTRY ALTER COLUMN STOCK_CODE TYPE VARCHAR(6);