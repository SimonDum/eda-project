# --- Stage 1 : Build Maven ---
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /project

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests

# --- Stage 2 : Runtime Image ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Création d'un utilisateur non-root pour la sécurité (Bonne pratique K8s)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=builder /project/target/*.jar app.jar

# Port exposé
EXPOSE 8080

# Point d'entrée
ENTRYPOINT ["java", "-jar", "app.jar"]