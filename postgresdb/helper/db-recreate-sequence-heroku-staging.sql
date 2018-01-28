/**
* COPY PRODUCTION TO STAGING
* heroku pg:copy savefy::DATABASE_URL postgresql-convex-43609 --app savefy-staging
*/

/**
* heroku pg:psql postgresql-convex-43609 --app savefy-staging < /Users/eriklacerda/Dev-Projects/savefy/postgresdb/helper/db-recreate-sequence-heroku-staging.sql
* Localhost  -- \i /Users/eriklacerda/Dev-Projects/savefy/postgresdb/helper/db-recreate-sequence-heroku-staging.sql
*/

CREATE SEQUENCE IF NOT EXISTS QUOTE_SALE_id_seq OWNED BY QUOTE_SALE.id;
ALTER TABLE QUOTE_SALE ALTER COLUMN id SET DEFAULT nextval('QUOTE_SALE_id_seq'::regclass);

CREATE SEQUENCE IF NOT EXISTS CONCILIATION_ENTRY_id_seq OWNED BY CONCILIATION_ENTRY.id;
ALTER TABLE CONCILIATION_ENTRY ALTER COLUMN id SET DEFAULT nextval('CONCILIATION_ENTRY_id_seq'::regclass);

CREATE SEQUENCE IF NOT EXISTS CONCILIATION_id_seq OWNED BY CONCILIATION.id;
ALTER TABLE CONCILIATION ALTER COLUMN id SET DEFAULT nextval('CONCILIATION_id_seq'::regclass);

CREATE SEQUENCE IF NOT EXISTS PATRIMONY_ENTRY_id_seq OWNED BY PATRIMONY_ENTRY.id;
ALTER TABLE PATRIMONY_ENTRY ALTER COLUMN id SET DEFAULT nextval('PATRIMONY_ENTRY_id_seq'::regclass);

CREATE SEQUENCE IF NOT EXISTS PATRIMONY_id_seq OWNED BY PATRIMONY.id;
ALTER TABLE PATRIMONY ALTER COLUMN id SET DEFAULT nextval('PATRIMONY_id_seq'::regclass);

CREATE SEQUENCE IF NOT EXISTS STOCK_SALE_id_seq OWNED BY STOCK_SALE.id;
ALTER TABLE STOCK_SALE ALTER COLUMN id SET DEFAULT nextval('STOCK_SALE_id_seq'::regclass);

CREATE SEQUENCE IF NOT EXISTS ACCOUNT_ENTRY_id_seq OWNED BY ACCOUNT_ENTRY.id;
ALTER TABLE ACCOUNT_ENTRY ALTER COLUMN id SET DEFAULT nextval('ACCOUNT_ENTRY_id_seq'::regclass);

CREATE SEQUENCE IF NOT EXISTS ACCOUNT_id_seq OWNED BY ACCOUNT.id;
ALTER TABLE ACCOUNT ALTER COLUMN id SET DEFAULT nextval('ACCOUNT_id_seq'::regclass);

CREATE SEQUENCE IF NOT EXISTS CATEGORY_PREDICTION_id_seq OWNED BY CATEGORY_PREDICTION.id;
ALTER TABLE CATEGORY_PREDICTION ALTER COLUMN id SET DEFAULT nextval('CATEGORY_PREDICTION_id_seq'::regclass);

CREATE SEQUENCE IF NOT EXISTS SANITIZE_PATTERN_id_seq OWNED BY SANITIZE_PATTERN.id;
ALTER TABLE SANITIZE_PATTERN ALTER COLUMN id SET DEFAULT nextval('SANITIZE_PATTERN_id_seq'::regclass);

CREATE SEQUENCE IF NOT EXISTS SUBCATEGORY_id_seq OWNED BY SUBCATEGORY.id;
ALTER TABLE SUBCATEGORY ALTER COLUMN id SET DEFAULT nextval('SUBCATEGORY_id_seq'::regclass);

CREATE SEQUENCE IF NOT EXISTS CATEGORY_id_seq OWNED BY CATEGORY.id;
ALTER TABLE CATEGORY ALTER COLUMN id SET DEFAULT nextval('CATEGORY_id_seq'::regclass);

CREATE SEQUENCE IF NOT EXISTS USER_PREFERENCES_id_seq OWNED BY USER_PREFERENCES.id;
ALTER TABLE USER_PREFERENCES ALTER COLUMN id SET DEFAULT nextval('USER_PREFERENCES_id_seq'::regclass);

CREATE SEQUENCE IF NOT EXISTS INSTITUTION_id_seq OWNED BY INSTITUTION.id;
ALTER TABLE INSTITUTION ALTER COLUMN id SET DEFAULT nextval('INSTITUTION_id_seq'::regclass);

CREATE SEQUENCE IF NOT EXISTS SUBCATEGORY_id_seq OWNED BY SUBCATEGORY.id;
ALTER TABLE SUBCATEGORY ALTER COLUMN id SET DEFAULT nextval('SUBCATEGORY_id_seq'::regclass);

CREATE SEQUENCE IF NOT EXISTS SUBCATEGORY_id_seq OWNED BY SUBCATEGORY.id;
ALTER TABLE SUBCATEGORY ALTER COLUMN id SET DEFAULT nextval('SUBCATEGORY_id_seq'::regclass);

 SELECT SETVAL('public.account_entry_id_seq', COALESCE(MAX(id), 1) ) FROM public.account_entry;
 SELECT SETVAL('public.account_id_seq', COALESCE(MAX(id), 1) ) FROM public.account;
 SELECT SETVAL('public.benchmark_id_seq', COALESCE(MAX(id), 1) ) FROM public.benchmark;
 SELECT SETVAL('public.category_id_seq', COALESCE(MAX(id), 1) ) FROM public.category;
 SELECT SETVAL('public.category_prediction_id_seq', COALESCE(MAX(id), 1) ) FROM public.category_prediction;
 SELECT SETVAL('public.conciliation_entry_id_seq', COALESCE(MAX(id), 1) ) FROM public.conciliation_entry;
 SELECT SETVAL('public.conciliation_id_seq', COALESCE(MAX(id), 1) ) FROM public.conciliation;
 SELECT SETVAL('public.institution_id_seq', COALESCE(MAX(id), 1) ) FROM public.institution;
 SELECT SETVAL('public.patrimony_entry_id_seq', COALESCE(MAX(id), 1) ) FROM public.patrimony_entry;
 SELECT SETVAL('public.patrimony_id_seq', COALESCE(MAX(id), 1) ) FROM public.patrimony;
 SELECT SETVAL('public.quote_sale_id_seq', COALESCE(MAX(id), 1) ) FROM public.quote_sale;
 SELECT SETVAL('public.sanitize_pattern_id_seq', COALESCE(MAX(id), 1) ) FROM public.sanitize_pattern;
 SELECT SETVAL('public.stock_sale_id_seq', COALESCE(MAX(id), 1) ) FROM public.stock_sale;
 SELECT SETVAL('public.subcategory_id_seq', COALESCE(MAX(id), 1) ) FROM public.subcategory;
 SELECT SETVAL('public.user_preferences_id_seq', COALESCE(MAX(id), 1) ) FROM public.user_preferences;