# Use an official OpenJDK runtime as a parent image
FROM maven:4.0.0-openjdk-21 AS
COPY . .
RUN mvn clean install

#
From eclipse-temurin:21-jdk
COPY --from=build /target/Practice_Springboot-0.0.1-SNAPSHOT.jar demo.jar

# Expose the port your app runs on (default is 8080)
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "demo.jar"]
