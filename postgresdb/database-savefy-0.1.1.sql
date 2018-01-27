/**
* Changeset 2017.01
* Criação da aplicação.
* heroku pg:psql postgresql-convex-43609 --app savefy-staging < /Users/eriklacerda/Dev-Projects/savefy/postgresdb/database-savefy-0.1.1.sql
* Localhost  -- \i /Users/eriklacerda/Dev-Projects/savefy/postgresdb/database-savefy-0.1.1.sql
*/

DROP TABLE IF EXISTS BENCHMARK;

CREATE TABLE BENCHMARK (
    ID                          SERIAL PRIMARY KEY,
    DATE        TIMESTAMP NOT NULL,
    CDI         DECIMAL,
    IBOVESPA    DECIMAL
);