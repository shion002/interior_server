# Step 1: Use OpenJDK 21 base image
FROM openjdk:21-jdk-slim

# Step 2: Set the working directory inside the container
WORKDIR /app

# Step 3: Copy the JAR file from your local machine to the container
COPY build/libs/interior-0.0.1-SNAPSHOT.jar /app/

# Step 4: Run the JAR file when the container starts
CMD ["java", "-jar", "-Dserver.port=10000", "interior-0.0.1-SNAPSHOT.jar"]
