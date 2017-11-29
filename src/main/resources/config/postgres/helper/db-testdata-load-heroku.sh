#!/usr/bin/env bash

heroku pg:psql --app ipocket
TRUNCATE TABLE user_role RESTART IDENTITY CASCADE;
TRUNCATE TABLE user_account RESTART IDENTITY CASCADE;
TRUNCATE TABLE user_group RESTART IDENTITY CASCADE;
\q

PGPASSWORD=4851528416fee096b8c895dbdae70c9c64defd4fa703a557447e4a8dad2fd477 psql -h ec2-50-17-201-204.compute-1.amazonaws.com -U qmespspfhewcbp d9pcua8t1pjv9b -c "\copy user_group FROM '/Users/eriklacerda/Dev-Projects/meEconomiza/src/main/resources/config/postgres/data/db-postgres-load-meeconomiza-group.csv' WITH CSV;"

PGPASSWORD=4851528416fee096b8c895dbdae70c9c64defd4fa703a557447e4a8dad2fd477 psql -h ec2-50-17-201-204.compute-1.amazonaws.com -U qmespspfhewcbp d9pcua8t1pjv9b -c "\copy user_account FROM '/Users/eriklacerda/Dev-Projects/meEconomiza/src/main/resources/config/postgres/data/db-postgres-load-meeconomiza-user_account.csv' WITH CSV;"

PGPASSWORD=4851528416fee096b8c895dbdae70c9c64defd4fa703a557447e4a8dad2fd477 psql -h ec2-50-17-201-204.compute-1.amazonaws.com -U qmespspfhewcbp d9pcua8t1pjv9b -c "\copy user_role FROM '/Users/eriklacerda/Dev-Projects/meEconomiza/src/main/resources/config/postgres/data/db-postgres-load-meeconomiza-roles.csv' WITH CSV;"




