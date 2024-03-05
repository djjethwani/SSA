FROM openjdk:17-jdk AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM openjdk:17-jdk-alpine
WORKDIR /app

COPY --from=build /app/target/SSA-0.0.1-SNAPSHOT.jar /app/SSA-0.0.1-SNAPSHOT.jar

CMD ["java", "-jar", "SSA-0.0.1-SNAPSHOT.jar"]