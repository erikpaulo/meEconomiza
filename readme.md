# Introdution

TBD - Erik will put some introdution here

# Setup instructions

## Required tools

- [Java 8][1]
- [Maven ][2]
- [Docker ][3] (optional)


## Compile and Run (docker local)

```command
mvn clean package
docker-compose -f stack.yml up
```

Docker will build two images like defined on stack.yml file. It takes a few minutes at the first time.

```yaml
version: '3.1'

services:
  postgres:
    build: postgresdb
    container_name: postgres-local
    ports:
      - "5432:5432"
    environment:
      POSTGRES_PASSWORD: admin

  application:
    build: .
    ports:
      - "8080:8080"

```

You can connect into postgres container using psql command. See some commands bellow

```
docker exec -it postgres-local psql -U postgres savefy
docker exec -it postgres-local bash
```

## Using postgres

cd postgresdb
docker build . -t meeconomiza_postgres:latest
docker run -p 5432:5432 -name postgres-local meeconomiza_postgres:latest

## Stuff used to make this:

[1]: http://java.com/en/
[2]: https://maven.apache.org/download.cgi
[3]: http://www.docker.com
