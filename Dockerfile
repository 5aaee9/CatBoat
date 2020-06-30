FROM openjdk:8-jre-slim
WORKDIR /app
COPY build/libs/CatBoat*.jar /app/CatBoat.jar

ENTRYPOINT [ "java" ]
CMD [ "-jar", "/app/CatBoat.jar" ]
