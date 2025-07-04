####################################################################
# 🛠️  BUILD STAGE
####################################################################
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace

# 1️⃣  Wrapper Maven + POM aggregatore  — importanti per la cache
COPY .mvn/ .mvn
COPY mvnw .
RUN chmod +x mvnw
COPY pom.xml .

# 2️⃣  Copio solo i POM dei singoli moduli per un primo “dependency go-offline”
#     (così Maven scarica le dipendenze una volta sola e le riusa finché i POM non cambiano)
COPY common/pom.xml               common/pom.xml
COPY api-gateway/pom.xml          api-gateway/pom.xml
COPY dispensation-service/pom.xml dispensation-service/pom.xml
COPY inventory-service/pom.xml    inventory-service/pom.xml
COPY notification-service/pom.xml notification-service/pom.xml
COPY prescription-service/pom.xml prescription-service/pom.xml

# Scarico in anticipo tutte le dipendenze
RUN ./mvnw -B dependency:go-offline

# 3️⃣  Copio l’intero sorgente (verrà invalidata la cache solo se cambia il codice)
COPY common                 common
COPY api-gateway            api-gateway
COPY dispensation-service   dispensation-service
COPY inventory-service      inventory-service
COPY notification-service   notification-service
COPY prescription-service   prescription-service

# 4️⃣  Compilo **solo** notification-service (+ i suoi moduli dipendenti) e skippo i test
RUN ./mvnw -B clean package \
       -pl notification-service \
       -am \
       -DskipTests

####################################################################
# 🚀  RUNTIME STAGE
####################################################################
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copio l’uber-jar (o il jar standard se non usi spring-boot-maven-plugin repackage)
COPY --from=build /workspace/notification-service/target/*.jar app.jar

# Porta HTTP di default (override con -e SERVER_PORT=...)
EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
