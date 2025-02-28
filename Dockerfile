# Step 1: Use OpenJDK 21 base image with gradle included
FROM gradle:8.5-jdk21 AS build

# Step 2: Set the working directory
WORKDIR /app

# Step 3: Copy the build files
COPY build.gradle settings.gradle /app/
COPY gradle /app/gradle
COPY gradlew gradlew.bat /app/

# Step 4: Copy the source code
COPY src /app/src

# Step 5: Build the application (skip tests)
RUN chmod +x ./gradlew
RUN ./gradlew build -x test --no-daemon

FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/build/libs/*.jar /app/app.jar

# Run the JAR directly
CMD ["java", "-jar", "app.jar"]
