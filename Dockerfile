# === Build ===
FROM gradle:8.7-jdk21-alpine AS build
WORKDIR /workspace
COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./
RUN ./gradlew --no-daemon build -x test || true
COPY src src
RUN ./gradlew --no-daemon clean bootJar -x test

# === Run ===
FROM eclipse-temurin:21-jre-alpine
ENV JAVA_OPTS=""
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
