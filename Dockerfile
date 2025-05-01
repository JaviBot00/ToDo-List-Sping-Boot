# Usa una imagen base con Java 17
FROM eclipse-temurin:17-jdk-alpine

# Copia el archivo .jar compilado
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Expone el puerto 8080
EXPOSE 8080

# Comando de inicio
ENTRYPOINT ["java", "-jar", "/app.jar"]
