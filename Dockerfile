# Stage 1: Build
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /build

COPY ecociclo/.mvn .mvn
COPY ecociclo/mvnw .
COPY ecociclo/pom.xml .

RUN chmod +x mvnw && ./mvnw dependency:go-offline -q

COPY ecociclo/src ./src

RUN ./mvnw clean package -DskipTests -q

# Stage 2: Runtime
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
