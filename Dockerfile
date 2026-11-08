#COPY pom.xml .
#COPY src ./src

#RUN mvn clean package -DskipTests
FROM eclipse-temurin:21-jre
LABEL authors="Annie"

WORKDIR /app

COPY target/campus-books-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
