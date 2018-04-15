FROM openjdk:8-jdk-alpine
WORKDIR /app/build
RUN apk add --no-cache maven
COPY . .
RUN mvn clean package

FROM openjdk:8-jre-alpine
WORKDIR /app/run
COPY --from=0 /build/target/ImageBot.jar .
ENTRYPOINT ["java", "-jar", "ImageBot.jar"]