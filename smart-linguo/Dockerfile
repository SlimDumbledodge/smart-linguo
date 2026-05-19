# ---- Étape 1 : Build ----
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copie des fichiers de dépendances en premier (cache Docker)
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copie du code source et build
COPY src ./src
RUN mvn package -DskipTests -q

# ---- Étape 2 : Image runtime ----
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/quarkus-app ./quarkus-app

EXPOSE 8080

CMD ["java", "-jar", "quarkus-app/quarkus-run.jar"]