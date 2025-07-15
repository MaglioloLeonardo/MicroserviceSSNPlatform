# ==================================================
# 🏗️ BUILD STAGE
# ==================================================
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace

# 1️⃣ Wrapper Maven + POM aggregatore
COPY .mvn/           .mvn
COPY mvnw            mvnw
RUN chmod +x mvnw
COPY pom.xml         pom.xml

# 2️⃣ Copio i POM di tutti i moduli per prime dipendenze
COPY common/pom.xml               common/pom.xml
COPY api-gateway/pom.xml          api-gateway/pom.xml
COPY dispensation-service/pom.xml dispensation-service/pom.xml
COPY inventory-service/pom.xml    inventory-service/pom.xml
COPY notification-service/pom.xml notification-service/pom.xml
COPY prescription-service/pom.xml prescription-service/pom.xml
COPY auth-service/pom.xml           auth-service/pom.xml

# Scarico tutte le dipendenze in anticipo
RUN ./mvnw -B dependency:go-offline

# 3️⃣ Copio l’intero sorgente
COPY common                 common
COPY api-gateway            api-gateway
COPY dispensation-service   dispensation-service
COPY inventory-service      inventory-service
COPY notification-service   notification-service
COPY prescription-service   prescription-
COPY auth-service/           auth-service/

# 4️⃣ Compilo solo dispensation-service (+ dipendenze interne) e skippo i test
RUN ./mvnw -B clean package \
       -pl dispensation-service \
       -am \
       -DskipTests -e

# ==================================================
# 🚀 RUNTIME STAGE
# ==================================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Serve wget per healthcheck
USER root
RUN apk add --no-cache wget

# Copio il jar
COPY --from=build /workspace/dispensation-service/target/*.jar app.jar

# Creo utente non-root
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

# Espongo porta e definisco healthcheck
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=2s \
  CMD wget -qO- http://localhost:8080/actuator/health \
      | grep '"status":"UP"' || exit 1

# Permette di passare opzioni JVM via env
ENV JAVA_OPTS=""
ENV JWT_SECRET=""

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
