/**
* Changeset 2017.01
* Criação da aplicação.
* heroku pg:psql --app savefy < /Users/eriklacerda/Projects/savefy/src/main/resources/config/postgres/db-postgres-savefy-2017.01.sql
* Localhost  -- \i /Users/eriklacerda/Dev-Projects/savefy/src/main/resources/config/postgres/data/db-postgres-savefy-2017.01.sql
*/


insert into category values (1, 'Transporte', 'EXP', 1);
insert into category values (2, 'Salário', 'INC', 1);
insert into subcategory values (1, 'Uber/Taxi/Cabify', TRUE, 'F', 1, 1);
insert into subcategory values (2, 'Combustível', TRUE, 'F', 1, 1);
insert into subcategory values (3, 'Estacionamento', TRUE, 'F', 1, 1);
insert into subcategory values (4, 'Carol', TRUE, 'F', 2, 1);
insert into account values (1, 'CC Personnalité', 'Itaú', 'P', 'CKA', TRUE, '1000', '2017-04-09', 1);
insert into account values (2, 'Visa Person', 'Itaú', 'N', 'CCA', TRUE, '0', '2017-04-09', 1);
INSERT INTO SANITIZE_PATTERN VALUES (1, '\d{2}\/\d{2}', '');
INSERT INTO SANITIZE_PATTERN VALUES (2, 'Uber UBER.*', 'Uber UBER');
