FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY . /app
RUN ./mvnw clean package -DskipTests

COPY target/SSA-0.0.1-SNAPSHOT.jar /app/SSA-0.0.1-SNAPSHOT.jar

CMD ["java", "-jar", "SSA-0.0.1-SNAPSHOT.jar"]