# --- STAGE 1: BASE ---
# Common setup and dependency caching layer
FROM maven:3.9.9-eclipse-temurin-21-alpine AS base
WORKDIR /app
# Copy only the POM first to take advantage of Docker layer caching
COPY pom.xml ./
# Download all dependencies offline so they are cached between builds
RUN mvn dependency:go-offline -B

# --- STAGE 2: DEVELOPMENT (For Local Dev) ---
# Fast feedback loop, mounts source code locally or runs directly with Spring Boot plugin
FROM base AS dev
COPY src ./src
EXPOSE 8080
# Enables live restart / devtools if configured in Spring Boot
CMD ["mvn", "spring-boot:run", "-Dspring-boot.run.jvmArguments='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005'"]

# --- STAGE 3: BUILDER (Optimization & Compilation) ---
# Compiles source and packages the production executable JAR
FROM base AS builder
COPY src ./src
RUN mvn clean package -Pprod -DskipTests

# Optional: Extract Spring Boot layered JAR layers for faster cold starts / smaller diffs
RUN java -Djarmode=layertools -jar target/*.jar extract --destination target/extracted

# --- STAGE 4: PRODUCTION ---
# Minimal JRE runtime image for Production / Cloud Run
FROM eclipse-temurin:21-jre-alpine AS prod
WORKDIR /app

# Security: Run as a non-root system user
RUN addgroup -g 1001 -S appgroup && adduser -u 1001 -S appuser -G appgroup

# Copy layers extracted from the builder stage (avoids hardcoding SNAPSHOT version names)
COPY --from=builder /app/target/extracted/dependencies/ ./
COPY --from=builder /app/target/extracted/spring-boot-loader/ ./
COPY --from=builder /app/target/extracted/snapshot-dependencies/ ./
COPY --from=builder /app/target/extracted/application/ ./

USER appuser

# Expose default HTTP port
EXPOSE 8080
ENV PORT=8080

# Production-tuned JVM flags (container memory awareness)
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "org.springframework.boot.loader.launch.JarLauncher"]