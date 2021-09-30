FROM maven:3.6.0-jdk-11-slim as MAVEN_BUILD
COPY ./ ./
RUN mvn clean package

FROM openjdk:11
COPY --from=MAVEN_BUILD target/webapp-0.0.1-SNAPSHOT.jar /webapp-0.0.1-SNAPSHOT.jar
COPY keystore.p12 /keystore.p12
CMD java -jar /webapp-0.0.1-SNAPSHOT.jar