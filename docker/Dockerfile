
# um von root im terminal das image zu bauen:  docker build -t robindockertest -f docker/Dockerfile .

# docker run --name robindockercontainer -p 8080:8080 robindockertest

# Stage 1: Build with Maven
FROM maven:3.9.6-amazoncorretto-21 AS build
WORKDIR /app
COPY pom.xml .
COPY backend/src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run with Amazon Corretto JRE
FROM amazoncorretto:21
WORKDIR /opt/exec
COPY --from=build /app/target/*.jar /opt/exec/app.jar
COPY --from=build /app/target/lib /opt/exec/lib
COPY frontend /opt/exec/frontend
EXPOSE 8080 5005
# Run with explicit classpath (app.jar + all jars in lib/)
CMD ["java", "-cp", "/opt/exec/app.jar:/opt/exec/lib/*", "Main"]

