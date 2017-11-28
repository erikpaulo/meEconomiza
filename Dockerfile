FROM java:8-jre
MAINTAINER Marcus Lacerda <marcus.lacerda@gmail.com>

COPY ./target/meEconomiza-2017.01.jar /app/meEconomiza-2017.01.war
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/meEconomiza-2017.01.war"]

EXPOSE 8080
