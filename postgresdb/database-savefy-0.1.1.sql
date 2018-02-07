/**
* heroku pg:psql postgresql-convex-43609 --app savefy-staging < /Users/eriklacerda/Dev-Projects/savefy/postgresdb/database-savefy-0.1.1.sql
* Localhost  -- \i /Users/eriklacerda/Dev-Projects/savefy/postgresdb/database-savefy-0.1.1.sql
*/

DROP TABLE IF EXISTS CONSOLIDATED_CASH_FLOW;

CREATE TABLE CONSOLIDATED_CASH_FLOW (
    ID                  SERIAL PRIMARY KEY,
    DATE                TIMESTAMP NOT NULL,
    NAME                VARCHAR(50),
    PARENT_ID           INTEGER NOT NULL REFERENCES CONSOLIDATED_CASH_FLOW(ID),
    TOTAL               DECIMAL NOT NULL,
    TOTAL_ANNUAL        DECIMAL NOT NULL,
    VARIATION           DECIMAL NOT NULL,
    AVERAGE             DECIMAL NOT NULL,
    AVERAGE_L3M         DECIMAL NOT NULL
);