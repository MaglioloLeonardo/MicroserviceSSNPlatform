# --------------------------------------------------
# 🏗  BUILD STAGE
# --------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace

# 1️⃣ Copio la Maven wrapper e il POM aggregatore
COPY .mvn/        .mvn
COPY mvnw         mvnw
RUN chmod +x mvnw
COPY pom.xml      pom.xml

# 2️⃣ Copio tutti i moduli del multi-module
COPY api-gateway/            api-gateway/
COPY dispensation-service/   dispensation-service/
COPY inventory-service/      inventory-service/
COPY notification-service/   notification-service/
COPY prescription-service/   prescription-service/
COPY auth-service/           auth-service/
COPY pharma-service/         pharma-service/
COPY anagrafica-service/         anagrafica-service/

# 3️⃣ “Fake” go-offline per scaldare la cache dei dependency layer
RUN ./mvnw -B -ntp dependency:go-offline

# 4️⃣ Build mirato SOLO per api-gateway (+ dipendenze interne -am)
RUN ./mvnw -B clean package \
      -pl api-gateway \
      -am \
      -DskipTests

# --------------------------------------------------
# 🚀  RUNTIME STAGE
# --------------------------------------------------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Installa wget (necessario per l’HEALTHCHECK)
USER root
RUN apk add --no-cache wget

# Copio il JAR appena generato
COPY --from=build /workspace/api-gateway/target/api-gateway-*.jar app.jar

# Evito di girare da root
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

# Porta e healthcheck
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=2s \
  CMD wget -qO- http://localhost:8080/actuator/health \
      | grep '"status":"UP"' || exit 1

# JVM options
ENV JAVA_OPTS=""
ENV JWT_SECRET=""

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -Dspring.cloud.compatibility-verifier.enabled=false -jar app.jar"]
