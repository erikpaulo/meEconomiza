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

Firt time docker will build 2 images like defined on stack.yml file.

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

You can connect into database container and use psql command to check your schema and make some queries.

docker exec -it meeconomiza_database_1 psql -U postgres


java -jar taret/meEconomiza-2017.01.jar

### Stuff used to make this:
[1]: http://java.com/en/
[2]: https://maven.apache.org/download.cgi
[3]: http://www.docker.com
