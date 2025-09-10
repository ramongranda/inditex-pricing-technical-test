# -------- Builder --------
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /workspace
COPY . .
RUN mvn -q -B -DskipTests -pl inditex-pricing-boot -am package
RUN cp inditex-pricing-boot/target/*.jar app.jar

# -------- Runtime --------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
USER 1000
COPY --from=builder /workspace/app.jar /app/app.jar
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java","-jar","/app/app.jar"]
