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

# Step 6: Create a lightweight image for running the application
FROM openjdk:21-jdk-slim

WORKDIR /app

# Step 7: Copy the JAR from the build stage
COPY --from=build /app/build/libs/interior-0.0.1-SNAPSHOT.jar /app/

# Step 8: Run the JAR when the container starts
CMD ["java", "-jar", "-Dserver.port=10000", "interior-0.0.1-SNAPSHOT.jar"]