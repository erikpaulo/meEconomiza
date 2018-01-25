#!/usr/bin/env bash

heroku pg:psql postgresql-cubic-67049 --app savefy
TRUNCATE TABLE SANITIZE_PATTERN RESTART IDENTITY CASCADE;
\q

PGPASSWORD=bab63ec4717744118e35b6e2bbd9cc19a2e01c6f67c4d5e4a9188bc94fdd43ee psql -h ec2-107-20-224-137.compute-1.amazonaws.com -U qmwbrqsgirnobf d8kghok0ujn8ov -c "\copy SANITIZE_PATTERN FROM '/Users/eriklacerda/Dev-Projects/savefy/postgresdb/data/system/db-postgres-load-sanitize-pattern.csv' WITH CSV;"

PGPASSWORD=bab63ec4717744118e35b6e2bbd9cc19a2e01c6f67c4d5e4a9188bc94fdd43ee psql -h ec2-107-20-224-137.compute-1.amazonaws.com -U qmwbrqsgirnobf d8kghok0ujn8ov -c "\copy INSTITUTION FROM '/Users/eriklacerda/Dev-Projects/savefy/postgresdb/data/system/db-postgres-load-institution.csv' WITH CSV;"




