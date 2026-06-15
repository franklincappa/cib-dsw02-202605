# ── Etapa 1: Build ──────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
LABEL authors="Franklin Cappa"

# Copia pom primero para cachear dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia el código fuente
COPY src ./src

# Compila y empaqueta sin tests
RUN mvn clean package -DskipTests -B

# ── Etapa 2: Runtime (imagen liviana) ───────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Usuario no-root por seguridad
RUN addgroup -S ventastech && adduser -S catalogo -G ventastech
USER catalogo

# Copia solo el jar generado
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
