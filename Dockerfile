FROM maven:3.6.0-jdk-8-slim AS build
COPY src /usr/src/app/src
COPY .git /usr/src/app/.git
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package

FROM openjdk:8-jre-alpine
COPY --from=build /usr/src/app/target/hr-management-api-*.jar /opt/hr-management-api.jar
CMD java -jar /opt/hr-management-api.jar
