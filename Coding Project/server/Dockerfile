
# Start with an official OpenJDK runtime
FROM maven:3.8.5-openjdk-17 as build

COPY . .

RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim

COPY --from=build /target/battleship.jar /app/battleship.jar

# Expose the port Spring Boot runs on
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "/app/battleship.jar"]