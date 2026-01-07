# ETAPA 1: Compilación (Build)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar solo el POM para descargar dependencias primero
# Esto se guarda en la caché de Docker y no se repite si el POM no cambia
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Ahora copiamos el código y compilamos
COPY src ./src
RUN mvn clean package -DskipTests

# ETAPA 2: Ejecución (Runtime)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Seguridad: Crear un usuario de sistema sin privilegios
RUN addgroup --system javauser && adduser --system --group javauser

# Optimización de la JVM para Contenedores
# MaxRAMPercentage evita que Java intente usar más RAM de la asignada al contenedor
ENV JAVA_OPTS="-XX:+UseParallelGC -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Copiar el JAR y cambiar el dueño al usuario javauser en un solo paso
COPY --from=build --chown=javauser:javauser /app/target/*.jar app.jar

# Usar el usuario no-root por seguridad
USER javauser

# Exponer el puerto (informativo)
EXPOSE 8080

# Usar exec para que las señales del sistema (como STOP) lleguen directo a Java
ENTRYPOINT ["java", "-jar", "app.jar"]