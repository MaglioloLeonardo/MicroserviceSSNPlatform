# === build stage ===
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /workspace

# 1) Copio wrapper (se li usi) e POM aggregatore
COPY .mvn        .mvn
COPY mvnw        mvnw
RUN chmod +x mvnw
COPY pom.xml     .

# 2) Copio **tutti** i moduli
COPY common                 common
COPY api-gateway            api-gateway
COPY dispensation-service   dispensation-service
COPY inventory-service      inventory-service
COPY notification-service   notification-service
COPY prescription-service   prescription-service

# 3) Build solo prescription-service e i suoi moduli “amici”
RUN mvn clean package \
      -pl prescription-service \
      -am \
      -DskipTests

# === runtime stage ===
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /workspace/prescription-service/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
