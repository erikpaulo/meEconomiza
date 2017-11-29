#!/usr/bin/env bash

heroku pg:psql postgresql-cubic-67049 --app meeconomiza
TRUNCATE TABLE SANITIZE_PATTERN RESTART IDENTITY CASCADE;
\q

PGPASSWORD=4851528416fee096b8c895dbdae70c9c64defd4fa703a557447e4a8dad2fd477 psql -h ec2-50-17-201-204.compute-1.amazonaws.com -U qmespspfhewcbp d9pcua8t1pjv9b -c "\copy SANITIZE_PATTERN FROM '../data/system/db-postgres-load-meeconomiza-sanitize-pattern.csv' WITH CSV;"




