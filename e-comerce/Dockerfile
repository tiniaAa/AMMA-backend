# Etapa 1: Construcción (Usamos Maven y Java 21)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos el pom y descargamos dependencias (esto cachea las dependencias)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiamos el código fuente y compilamos
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Usamos una imagen de Java más liviana)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiamos el archivo .jar que se compiló en la etapa 1
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto
EXPOSE 8080

# Comando para arrancar Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]