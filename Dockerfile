# Etapa 1: Compilar el proyecto con Maven
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copiar pom.xml y descargar dependencias primero (cache eficiente)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Crear una imagen ligera para ejecutar la app
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copiar el jar compilado desde la primera etapa
COPY --from=builder /app/target/*.jar app.jar

# Puerto de ejecución
EXPOSE 8080

# Comando para ejecutar
ENTRYPOINT ["java", "-jar", "app.jar"]
