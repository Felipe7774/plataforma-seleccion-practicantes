FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml ./
COPY src ./src
RUN mvn --batch-mode --no-transfer-progress package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN useradd --system --create-home --uid 10001 appuser
COPY --from=build /workspace/target/backend-0.0.1-SNAPSHOT.jar app.jar

USER appuser
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "if [ -n \"$DATABASE_URL\" ] && [ \"${DATABASE_URL#jdbc:}\" = \"$DATABASE_URL\" ]; then export SPRING_DATASOURCE_URL=\"jdbc:$DATABASE_URL\"; fi; exec java -jar /app/app.jar"]
