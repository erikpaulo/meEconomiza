#!/usr/bin/env bash

heroku pg:psql postgresql-cubic-67049 --app savefy
TRUNCATE TABLE SANITIZE_PATTERN RESTART IDENTITY CASCADE;
\q

PGPASSWORD=b272d30609c8c5f8a64aa83b0bc06d3fb36ff3a677cb919e8853bc5553b2d358 psql -h ec2-54-235-210-115.compute-1.amazonaws.com -U cvsxhyjahnnuxe df5cja041q5ij1 -c "\copy SANITIZE_PATTERN FROM '/Users/eriklacerda/Dev-Projects/savefy/postgresdb/data/system/db-postgres-load-sanitize-pattern.csv' WITH CSV;"

PGPASSWORD=b272d30609c8c5f8a64aa83b0bc06d3fb36ff3a677cb919e8853bc5553b2d358 psql -h ec2-54-235-210-115.compute-1.amazonaws.com -U cvsxhyjahnnuxe df5cja041q5ij1 -c "\copy INSTITUTION FROM '/Users/eriklacerda/Dev-Projects/savefy/postgresdb/data/system/db-postgres-load-institution.csv' WITH CSV;"




