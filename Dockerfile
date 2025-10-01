# STAGE 1: THE BUILD STAGE
FROM gradle:8.12.1-jdk21 AS build

WORKDIR /app

# Copy root build files and Gradle wrapper
COPY settings.gradle .
COPY gradlew .
COPY gradlew.bat .
COPY gradle gradle

# Copy only the build.gradle files to leverage dependency caching
COPY Sazzler-Auth-Service/build.gradle ./Sazzler-Auth-Service/
COPY api-definition/build.gradle ./api-definition/
COPY util/build.gradle ./util/

# Download dependencies first
RUN ./gradlew dependencies --no-daemon

# Now copy the entire source code for all modules
COPY Sazzler-Auth-Service ./Sazzler-Auth-Service
COPY api-definition ./api-definition
COPY util ./util

# Build the specific service, skipping tests
RUN ./gradlew :Sazzler-Auth-Service:build --no-daemon -x test


# THE RUNTIME STAGE
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

# Copy the final JAR from the correct subproject's build directory
COPY --from=build /app/Sazzler-Auth-Service/build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","app.jar"]
