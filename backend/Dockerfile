# ─── Etapa 1: Build ───────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copiamos primero solo el pom.xml para aprovechar el cache de capas de Docker.
# Si el código cambia pero el pom.xml no, Docker reutiliza la capa de dependencias
# sin volver a descargarlas.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Ahora copiamos el código fuente y compilamos
COPY src ./src
RUN mvn package -DskipTests -B

# ─── Etapa 2: Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

# Usuario no-root por seguridad — nunca correr como root en producción
RUN addgroup -S vantan && adduser -S vantan -G vantan
USER vantan

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]