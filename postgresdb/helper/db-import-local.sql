/**
* Localhost  -- \i /Users/eriklacerda/Dev-Projects/savefy/postgresdb/helper/db-import-local.sql
*/

COPY INSTITUTION FROM '/Users/eriklacerda/Dev-Projects/savefy/postgresdb/data/system/db-postgres-load-institution.csv' DELIMITER ',' CSV;
COPY SANITIZE_PATTERN FROM '/Users/eriklacerda/Dev-Projects/savefy/postgresdb/data/system/db-postgres-load-sanitize-pattern.csv' DELIMITER ',' CSV;
copy USER_PREFERENCES from '/Users/eriklacerda/Dev-Projects/savefy/postgresdb/dump/USER_PREFERENCES.csv' WITH (DELIMITER ',', NULL '');

copy CATEGORY from '/Users/eriklacerda/Dev-Projects/savefy/postgresdb/dump/CATEGORY.csv' WITH (DELIMITER ',', NULL '');
copy SUBCATEGORY from '/Users/eriklacerda/Dev-Projects/savefy/postgresdb/dump/SUBCATEGORY.csv' WITH (DELIMITER ',', NULL '');
copy CATEGORY_PREDICTION from '/Users/eriklacerda/Dev-Projects/savefy/postgresdb/dump/CATEGORY_PREDICTION.csv' WITH (DELIMITER ',', NULL '');
copy ACCOUNT from '/Users/eriklacerda/Dev-Projects/savefy/postgresdb/dump/ACCOUNT.csv' WITH (DELIMITER ',', NULL '');
copy ACCOUNT_ENTRY from '/Users/eriklacerda/Dev-Projects/savefy/postgresdb/dump/ACCOUNT_ENTRY.csv' WITH (DELIMITER ',', NULL '');
copy CONCILIATION from '/Users/eriklacerda/Dev-Projects/savefy/postgresdb/dump/CONCILIATION.csv' WITH (DELIMITER ',', NULL '');
copy CONCILIATION_ENTRY from '/Users/eriklacerda/Dev-Projects/savefy/postgresdb/dump/CONCILIATION_ENTRY.csv' WITH (DELIMITER ',', NULL '');
copy PATRIMONY from '/Users/eriklacerda/Dev-Projects/savefy/postgresdb/dump/PATRIMONY.csv' WITH (DELIMITER ',', NULL '');
copy PATRIMONY_ENTRY from '/Users/eriklacerda/Dev-Projects/savefy/postgresdb/dump/PATRIMONY_ENTRY.csv' WITH (DELIMITER ',', NULL '');
copy QUOTE_SALE from '/Users/eriklacerda/Dev-Projects/savefy/postgresdb/dump/QUOTE_SALE.csv' WITH (DELIMITER ',', NULL '');
copy STOCK_SALE from '/Users/eriklacerda/Dev-Projects/savefy/postgresdb/dump/STOCK_SALE.csv' WITH (DELIMITER ',', NULL '');

