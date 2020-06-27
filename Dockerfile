FROM openjdk:8-jdk-slim as builder

COPY . /app
WORKDIR /app
RUN ./gradlew shadowJar

FROM openjdk:8-jre-slim
WORKDIR /app
COPY --from=builder /app/build/lib/CatBoat*.jar /app/CatBoat.jar

ENTRYPOINT [ "java" ]
CMD [ "-jar /app/CatBoat.jar" ]
