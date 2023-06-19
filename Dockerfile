FROM maven:3.9.0-eclipse-temurin-19-alpine
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean install -Punit-test
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/home/app/target/statly-backend-api-0.0.1-SNAPSHOT.jar"]