
# um von root im terminal das image zu bauen:  docker build -t robindockertest -f docker/Dockerfile .

# docker run --name robindockercontainer -p 8080:8080 robindockertest

# Stage 1: Build with Maven
FROM maven:3.9.6-amazoncorretto-21 AS build
WORKDIR /app
COPY pom.xml .
COPY backend/src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run with Amazon Corretto
FROM amazoncorretto:21
WORKDIR /opt/exec
COPY --from=build /app/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]
